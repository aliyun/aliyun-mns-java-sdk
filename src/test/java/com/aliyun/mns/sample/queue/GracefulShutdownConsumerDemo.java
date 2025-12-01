package com.aliyun.mns.sample.queue;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.client.MNSClientBuilder;
import com.aliyun.mns.common.auth.SignVersion;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Message Queue Consumer Graceful Shutdown Example
 * 
 * This example demonstrates how to gracefully shut down message consumers in a multi-threaded environment.
 * It shows the difference between graceful shutdown (good practice) and direct forced shutdown (bad practice).
 * 
 * Good Case (Graceful Shutdown):
 * - Allows ongoing tasks to complete naturally
 * - Waits for tasks to finish before terminating threads
 * - Minimizes message loss and ensures data consistency
 * 
 * Bad Case (Direct Shutdown):
 * - Immediately interrupts all running tasks
 * - May cause message processing interruption and data inconsistency
 * - Faster but riskier approach
 */
public class GracefulShutdownConsumerDemo {

    private static final String QUEUE_NAME = "GracefulShutdownTestQueue";
    private static volatile boolean shutdownRequested = false;

    public static void main(String[] args) {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignatureVersion(SignVersion.V4);
        MNSClient client = MNSClientBuilder.create()
            .accountEndpoint(ServiceSettings.getMNSAccountEndpoint()) // eg: http://123.mns.cn-hangzhou.aliyuncs.com
            .clientConfiguration(clientConfig)
            .region(ServiceSettings.getMNSRegion()) // eg: "cn-hangzhou"
            .build();

        CloudQueue queue = client.getQueueRef(QUEUE_NAME);

        // Create a fixed-size thread pool for message processing
        ExecutorService consumerExecutor = Executors.newFixedThreadPool(5);

        // Register shutdown hook to implement graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutdown signal received, starting shutdown process...");
            // Toggle between these two methods to see the difference

            gracefulShutdown(consumerExecutor, client); // Good case - graceful shutdown

             //directShutdown(consumerExecutor, client); // Bad case - direct forced shutdown

        }));

        // Create queue if it doesn't exist
        queue.create();
        System.out.println("Starting to consume messages...");

        // Start multiple consumer threads
        for (int i = 0; i < 3; i++) {
            final int threadIndex = i;
            consumerExecutor.submit(() -> consumeMessages(queue, threadIndex));
        }

        // Keep main thread running until interrupted
        // This loop prevents the main thread from exiting and keeps the application running
        // The application will only exit when:
        // 1. User presses Ctrl+C (handled by shutdown hook)
        // 2. Process receives termination signal (kill command or IDE stop button)
        // 3. Main thread is interrupted (handled in InterruptedException catch block)
        while (!shutdownRequested) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted, preparing to shut down...");
                break;
            }
        }
    }

    /**
     * Graceful Shutdown (Good Practice)
     * 
     * This method shuts down the thread pool gracefully, allowing ongoing tasks to complete.
     * 
     * Process:
     * 1. Stop accepting new tasks but let existing tasks continue
     * 2. Wait up to 30 seconds for tasks to complete naturally
     * 3. If tasks don't complete within timeout, forcefully terminate them
     * 4. Close the MNS client to release resources
     * 
     * Benefits:
     * - Ensures message processing completes properly
     * - Minimizes message loss
     * - Maintains data consistency
     * 
     * @param executor Thread pool to shut down
     * @param client   MNS client to close
     */
    private static void gracefulShutdown(ExecutorService executor, MNSClient client) {
        // Execute common shutdown steps with graceful approach
        executeShutdown(executor, client, true);
    }

    /**
     * Direct Forced Shutdown (Bad Practice)
     * 
     * This method immediately terminates all threads without allowing tasks to complete.
     * 
     * Process:
     * 1. Immediately interrupt all running tasks
     * 2. Immediately close the MNS client
     * 
     * Risks:
     * - Message processing may be interrupted
     * - Messages may be lost or processed incompletely
     * - Data inconsistency may occur
     * 
     * @param executor Thread pool to shut down
     * @param client   MNS client to close
     */
    private static void directShutdown(ExecutorService executor, MNSClient client) {
        // Execute common shutdown steps with direct approach
        executeShutdown(executor, client, false);
    }

    /**
     * Common shutdown logic that handles both graceful and direct shutdown approaches.
     * 
     * @param executor     Thread pool to shut down
     * @param client       MNS client to close
     * @param gracefulMode True for graceful shutdown, false for direct forced shutdown
     */
    private static void executeShutdown(ExecutorService executor, MNSClient client, boolean gracefulMode) {
        // Check if shutdown has already been requested
        if (shutdownRequested) return;
        shutdownRequested = true;
        
        if (gracefulMode) {
            System.out.println("Performing graceful shutdown of consumer threads...");
            performGracefulThreadPoolShutdown(executor);
        } else {
            System.out.println("Performing direct forced shutdown (not recommended)...");
            performDirectThreadPoolShutdown(executor);
        }
        
        // Close the MNS client to release resources
        closeMNSClient(client);
        
        // Print completion message based on shutdown mode
        if (gracefulMode) {
            System.out.println("MNS client closed gracefully - all resources released properly");
        } else {
            System.out.println("MNS client closed forcefully - resources released immediately");
        }
    }

    /**
     * Performs graceful shutdown of thread pool, allowing tasks to complete naturally.
     * 
     * @param executor Thread pool to shut down
     */
    private static void performGracefulThreadPoolShutdown(ExecutorService executor) {
        // Step 1: Stop accepting new tasks (but allow existing tasks to complete)
        executor.shutdown();
        
        try {
            // Step 2: Wait up to 30 seconds for tasks to complete naturally
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("Some tasks did not complete in time, attempting forced shutdown...");
                
                // Step 3: Forcefully terminate remaining tasks
                executor.shutdownNow();
                
                // Wait another 5 seconds for forced shutdown to complete
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Thread pool could not be fully shut down");
                }
            }
        } catch (InterruptedException e) {
            // If interrupted, immediately force shutdown
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Performs direct forced shutdown of thread pool, immediately interrupting all tasks.
     * 
     * @param executor Thread pool to shut down
     */
    private static void performDirectThreadPoolShutdown(ExecutorService executor) {
        // Immediately stop all executing tasks and reject new tasks
        if (executor != null && !executor.isTerminated()) {
            executor.shutdownNow();
            System.out.println("Called executor.shutdownNow() to interrupt all threads");
        }
    }

    /**
     * Closes the MNS client to release resources.
     * 
     * @param client MNS client to close
     */
    private static void closeMNSClient(MNSClient client) {
        // Close the MNS client to release resources
        if (client != null) {
            client.close();
        }
    }

    /**
     * Consume messages from the queue
     * 
     * This method simulates message consumption in a loop until shutdown is requested.
     * 
     * @param queue       Queue reference
     * @param threadIndex Thread index for identification
     */
    private static void consumeMessages(CloudQueue queue, int threadIndex) {
        System.out.println("Consumer thread-" + threadIndex + " started working");
        
        while (!shutdownRequested) {
            try {
                // Long-poll for messages with 10-second timeout
                Message message = queue.popMessage(10);
                if (message != null) {
                    System.out.println("Consumer thread-" + threadIndex + " received message: " + message.getMessageId());

                    // Delete processed message
                    queue.deleteMessage(message.getReceiptHandle());
                    System.out.println("Consumer thread-" + threadIndex + " processed and deleted message: " + message.getMessageId());
                }
           } catch (Exception e) {
                System.err.println("Thread " + threadIndex + " error consuming message: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Thread " + threadIndex + " exited consumption loop");
    }
}