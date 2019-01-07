package com.dovar.plugin


class ComponentReader{

    def manifest

    ComponentReader(String filePath) {
        manifest = new XmlSlurper().parse(filePath)
    }

    private void addProcess(Set<String> processNames, def it) {
        String processName = it.'@android:process'
        if (processName != null && processName.length() > 0) {
            processNames.add(processName)
        }
    }

    void readActivities(Set<String> processNames) {
        manifest.application.activity.each {
            addProcess(processNames, it)
        }
    }

    void readServices(Set<String> processNames) {
        manifest.application.service.each {
            addProcess(processNames, it)
        }
    }

    void readBroadcastReceivers(Set<String> processNames) {
        manifest.application.receiver.each {
            addProcess(processNames, it)
        }
    }

    void readProviders(Set<String> processNames) {
        manifest.application.provider.each {
            addProcess(processNames, it)
        }
    }
}