/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * TransactionQueue that provide sendPrepareMessage， commitMessage， rollbackMessage for transaction。
 */
package com.aliyun.mns.client;

import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.ServiceHandlingRequiredException;
import com.aliyun.mns.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  The product no longer supports this function
 * 2025.05 当前事务存在 业务执行耗时短导致概率性丢消息的问题，不建议使用
 * 若有强烈的事务诉求，辛苦提工单，排期用更优方案支持
 */
@Deprecated
public class TransactionQueue {
    private static Logger log = LoggerFactory.getLogger(TransactionQueue.class);
    private CloudQueue innerQueue;
    private CloudQueue opLogQueue;
    private TransactionChecker tChecker;
    private boolean isCheckerStop;
    private long checkIntervalInMillisecond;
    private Thread checkerThread;
    private long lifeTime;
    private long delayTime;
    private int transactionTimeoutInSecond;

    public static long DEFAULT_LIFE_TIME_IN_SECONDS = 47 * 3600; // 47 hours
    public static long DEFAULT_DELAY＿TIME_IN_SECONDS = 48 * 3600; // 48 hours

    /*
     * Send the operation log of transaction to operation log queue.
     */
    private Message sendOpLogMessage(String transHandler) throws ServiceException {
        Message message = new Message();
        message.setMessageBody(transHandler);
        message.setDelaySeconds(this.transactionTimeoutInSecond);
        message = this.opLogQueue.putMessage(message);

        return message;
    }

    /*
     * confirm operation log message by message or its handler.
     * It will delete the operation log message in queue, which means the operation is success.
     */
    private void confirmOpLogMessage(Message message) {
        try {
            this.opLogQueue.deleteMessage(message.getReceiptHandle());
        } catch (Exception e) {
            log.warn("confirmOpLogMessage message:" + message.getReceiptHandle() + " failed.");
            //transactions timeout. ignore. transaction checker thread will deal this.
        }
    }

    /*
     * sleep.
     */
    private void mySleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ie) {
            // corner case.
            log.error("sleep interrupted error:" + ie.getMessage());
        }
    }

    /*
     * Class for background thread to run in order to check the transaction operation log.
     */
    private class CheckTransactionMesssage implements Runnable {
        public void run() {
            log.info("CheckTransactionMesssage thread start");
            while (!isCheckerStop) {
                try {
                    Message opLogMessage = opLogQueue.popMessage();// use long polling.
                    Message transMessage = null;
                    if (opLogMessage != null) {
                        log.info("get an op log for message:" + opLogMessage.getMessageBody());
                        transMessage = new Message();
                        transMessage.setReceiptHandle(opLogMessage.getMessageBody());

                        boolean isTransSuccess = false;
                        try {
                            isTransSuccess = tChecker.checkTransactionStatus(transMessage);
                        } catch (Exception e) {
                            log.error("exception occurs when doing checkTransactionStatus with:"
                                + opLogMessage.getMessageBody()
                                + "exception message is:" + e.getMessage());
                        }

                        if (isTransSuccess) {   // transaction success, commit the message.
                            commitMessage(transMessage);
                        } else {
                            // transaction fail, rollback message and confirm op log.
                            rollbackMessage(transMessage);
                        }
                        confirmOpLogMessage(opLogMessage);
                    }
                    //else: in most time, operation log queue is empty and nothing need to do.
                } catch (Exception e) {
                    log.error("exception occurs:" + e.getMessage());
                    e.printStackTrace();
                }

                //sleep.
                mySleep(checkIntervalInMillisecond);

            }
            log.info("CheckTransactionMesssage thread end");
        }
    }

    /*
     * Constructor.
     *
     * @param rawQueue the real cloudQueue.
     * @param lifeTimeInSeconds message life time.
     * @param delayTimeInSeconds message delay time.
     *
     * Note: if lifeTimeInSeconds <= delayTimeInSeconds: message will never be
     *  visible to consumer otherwise we change the visibility time.
     *  if  lifeTimeInSeconds > delayTimeInSeconds:the message has chance to be
     *  visible to consumer.
     */
    public TransactionQueue(CloudQueue rawQueue, CloudQueue opLogQueue, TransactionChecker checker,
        long lifeTimeInSeconds,
        long delayTimeInSeconds) {
        this.innerQueue = rawQueue;
        this.opLogQueue = opLogQueue;
        this.tChecker = checker;
        this.lifeTime = lifeTimeInSeconds;
        this.delayTime = delayTimeInSeconds;
        this.isCheckerStop = false;
        this.checkIntervalInMillisecond = 5000;  //5s
        this.transactionTimeoutInSecond = 600; // 600s

        if (this.tChecker != null) {
            this.checkerThread = new Thread(new CheckTransactionMesssage());
            checkerThread.start();
        }
    }

    public void finalize() {
        this.stopCheckThread();
    }

    /*
     * Send message which is not visible to consumer at once.
     *
     * @param message the info of message to send.
     */
    public Message sendPrepareMessage(Message message) throws ServiceException,
        ClientException {
        return this.innerQueue.putMessage(message);
    }

    /*
     * Commit the prepare message then the message will be visible to consumer in seconds.
     *
     * @param receiptHandle the handler of prepare message.
     * @param retryTimes count of commit operation will be tried when error occurs.
     */
    public void commitMessage(String receiptHandle, int retryTimes) throws ServiceException,
        ClientException {
        int i = 0;
        while (i < retryTimes) {
            i++;
            try {
                this.innerQueue.changeMessageVisibility(receiptHandle, 1);
                break;
            } catch (ServiceException se) {
                //if message not exist, need not retry.
                if (se.getErrorCode().equals("MessageNotExist")) {
                    break;
                }
            } catch (ClientException ce) {
            }

            //sleep 1s between retry commit operation.
            this.mySleep(1000);
        }
    }

    /*
     * Commit the prepare message then the message will be visible to consumer in seconds.
     *
     * @param message the info of message to commit.
     * @param retryTimes count of commit operation will be tried when error occurs.
     */
    public void commitMessage(Message message, int retryTimes) throws ServiceException,
        ClientException {
        this.commitMessage(message.getReceiptHandle(), retryTimes);
    }

    /*
     * Retry 3 times by default.
     */
    public void commitMessage(String receiptHandle) throws ServiceException,
        ClientException {
        this.commitMessage(receiptHandle, 3);
    }

    /*
     * Retry 3 times by default.
     */
    public void commitMessage(Message message) throws ServiceException,
        ClientException {
        this.commitMessage(message.getReceiptHandle(), 3);
    }

    /*
     * Rollback the message by given handler to make sure the message is never be visible to consumer.
     *
     * @param receiptHandle  the message handle to rollback.
     *
     * note: if lifeTimeInSeconds <= delayTimeInSeconds, this operation can be ignore.
     */
    public void rollbackMessage(String receiptHandle) throws ServiceException,
        ClientException {
        try {
            this.innerQueue.deleteMessage(receiptHandle);
        } catch (ServiceException se) {
            if (!se.getErrorCode().equals("MessageNotExist")) {
                throw se;
            } else {
                //assume the message have already been delete (rollback success) if message not exist
            }
        } catch (ServiceHandlingRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * roll-back the message by message object.
     * @param message the message info to roll-back.
     */
    public void rollbackMessage(Message message) throws ServiceException,
        ClientException {
        this.rollbackMessage(message.getReceiptHandle());
    }

    /*
     * Send transaction message.
     * @param message the transaction message to send.
     * @operations the operations provide by user.
     */
    public Message sendTransMessage(Message message, TransactionOperations operations) throws ServiceException,
        ClientException {
        String handler = null;
        Message prepareMsg = null;
        Message opLogMessage = null;
        boolean localOpResult = false;

        prepareMsg = this.sendPrepareMessage(message);
        if (prepareMsg != null) {
            handler = prepareMsg.getReceiptHandle();
            prepareMsg.setMessageBody(message.getMessageBody());
        } else {
            return prepareMsg;
        }

        // write operation log to op queue before do transaction.
        opLogMessage = this.sendOpLogMessage(handler);

        //do local transaction operation.
        try {
            localOpResult = operations.doTransaction(prepareMsg);
        } catch (Exception e) {
            log.error("exception occurs when do transaction with message:"
                + prepareMsg.getMessageBody() + ", message handler is:"
                + prepareMsg.getReceiptHandle());
            localOpResult = false;
        }

        if (localOpResult) {
            //commit message, it will retry 3 times by default if it was fail.
            this.commitMessage(handler);
        } else {
            this.rollbackMessage(handler);
            prepareMsg = null;
        }

        // confirm the operation log message.
        this.confirmOpLogMessage(opLogMessage);

        return prepareMsg;
    }

    /*
     * Get inner queue to do other operations on the queue.
     */
    public CloudQueue getInnerQueue() {
        return this.innerQueue;
    }

    /*
     * stop background check thread, and delete transaction queue and operation log queue.
     *
     * @param needDeleteOpLogQueue set it tree to delete operation log queue at the same time.
     */
    public void delete(boolean needDeleteOpLogQueue) throws ServiceException {
        this.stopCheckThread();
        this.innerQueue.delete();
        if (needDeleteOpLogQueue) {
            this.opLogQueue.delete();
        }
    }

    /*
     * stop background check thread, and delete transaction queue and operation log queue.
     */
    public void delete() throws ServiceException {
        this.delete(true);
    }

    /*
     * Get the message life time.
     */
    public long getLifeTime() {
        return this.lifeTime;
    }

    /*
     * Get the message delay time.
     */
    public long getDelayTime() {
        return this.delayTime;
    }

    /*
     * get the operation log queue for transaction.
     *
     * user can get this queue and use it to check if there is any in complete transaction message.
     */
    public CloudQueue getTransOpLogQueue() {
        return this.opLogQueue;
    }

    /*
     * Stop check thread.
     */
    public void stopCheckThread() {
        this.isCheckerStop = true;
    }

    /*
     * Get check interval time.
     */
    public long getCheckIntervalInMillsecond() {
        return this.checkIntervalInMillisecond;
    }

    /*
     * Set check interval time.
     */
    public void setCheckIntervalInMillsecond(long checkIntervalInMillsecond) {
        this.checkIntervalInMillisecond = checkIntervalInMillsecond;
    }

    /*
     * Get check interval time.
     */
    public int getTransactionTimeoutInSecond() {
        return this.transactionTimeoutInSecond;
    }

    /*
     * Set transaction timeout.
     */
    public void setTransactionTimeoutInSecond(int seconds) {
        this.transactionTimeoutInSecond = seconds;
    }
}