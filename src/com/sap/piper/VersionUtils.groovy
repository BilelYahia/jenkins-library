package com.sap.piper

import hudson.AbortException


class VersionUtils implements Serializable {

    def static verifyVersion(script, name, executable, String version, versionOption) {

        script.echo "Verifying $name version $version or compatible version."

        def toolVersion
        try {
          toolVersion = script.sh returnStdout: true, script: """#!/bin/bash
                                                                 $executable $versionOption"""
        } catch(AbortException e) {
          throw new AbortException("The verification of $name failed. Please check '$executable'. $e.message.")
        }
        def installedVersion = new Version(toolVersion)
        if (!installedVersion.isCompatibleVersion(new Version(version))) {
          throw new AbortException("The installed version of $name is ${installedVersion.toString()}. Please install version $version or a compatible version.")
        }
        script.echo "Verification success. $name version ${installedVersion.toString()} is installed."
    }

    def static verifyVersion(script, name, executable, Map versions, versionOption) {

        def toolVersion
        try {
          toolVersion = script.sh returnStdout: true, script: """#!/bin/bash
                                                                 $executable $versionOption"""
        } catch(AbortException e) {
          throw new AbortException("The verification of $name failed. Please check '$executable'. $e.message.")
        }
        for (def entry : versions) {
            if (toolVersion.contains(entry.getKey())) {
                def installedVersion = new Version(toolVersion)
                def expectedVersion = entry.getValue()
                script.echo "Verifying $name version $expectedVersion or compatible version."
                if (!installedVersion.isCompatibleVersion(new Version(expectedVersion))) {
                    throw new AbortException("The installed version of $name is ${installedVersion.toString()}. Please install version $expectedVersion or a compatible version.")
                }
                script.echo "Verification success. $name version ${installedVersion.toString()} is installed."
            }
        }
        script.echo "Verification success."
    }
}
