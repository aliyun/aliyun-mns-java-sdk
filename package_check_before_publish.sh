#!/bin/sh

# This script packages the MNS Java SDK before publishing to Maven Central

# -Dmaven.test.skip=true: Skip running and compiling test cases
# clean: Clean previously compiled classes and artifacts
# source:jar: Package source code in a jar file
# javadoc:jar: Generate and package javadoc in a jar file
# package: Compile and package the main artifacts

mvn clean source:jar javadoc:jar package -Dmaven.test.skip=true  >package.txt