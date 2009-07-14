The replication services available on 5.2 are import and export. They are independent one of each other. They are also available either though JSF UI or through a MBean visible in JMX console. 
The UI is available only if the web component is also deployed.
The feature code is currently located in sandbox project nuxeo-platform-replication (as also nuxeo-platform-importer feature, which is used inside replication). In order to have it deployed the following jars needs to be deployed:
Export
nuxeo-platform-replication-common-api-5.2.1.jar    
nuxeo-platform-replication-exporter-core-5.2.1.jar 
nuxeo-platform-replication-exporter-api-5.2.1.jar  
nuxeo-platform-replication-exporter-web-5.2.1.jar
Import
nuxeo-platform-replication-common-api-5.2.1.jar    
nuxeo-platform-replication-importer-core-5.2.1.jar 
nuxeo-platform-replication-importer-api-5.2.1.jar  
nuxeo-platform-replication-importer-web-5.2.1.jar

Export instructions – JSF UI
The link “Export” is present in the top list of actions. The page allows selecting the destination of the archive. The exported artefacts are stored here, so enough space must be ensured.
Once the export launched (pressing once on the link “Export”) the page displays the progress, updating the number of documents exported in top of the page. At the end of export, status “Done” is displayed.
Please be patient and don't press twice the link.
Also, the status and information about the export can be seen in the server log.
Export instructions -- MBean
The MBean is available in JMX console as Exporter service. It offers a single method: export, with 2 parameteres. First one is the repository name (usually "default") and second is the path where the export is perfomed.
For the results, watch the server logs.

Import instructions – JSF UI
The link “Import” is present in the top list of actions. The page allows selecting the source of the archive.
Once the import launched (pressing once on the link “Import”) the page displays the progress, updating the number of documents imported in top of the page. At the end of import, status “Done” is displayed.
Please be patient and don't press twice the link.
Also, the status and information about the import can be seen in the server log.
Import instructions -- MBean
The MBean is available in JMX console as Exporter service. It offers a single method: import, with 1 parameter: the source archive path.
For the results, watch the server logs.
