Step 1 - Set the AZURE_OPENAI_KEY, AZURE_OPENAI_ENDPOINT, and AZURE_OPENAI_MODEL_ID environment variables with your actual Azure OpenAI key, endpoint, and model ID before starting Tomcat. You can set these variables in the script that you use to start Tomcat, or in your system's environment variables.
Step 2 - Deploy the application to Tomcat: Copy the .war file to the webapp
installation. Tomcat will automatically deploy the application.
Step 3 - Create a service principal for the AZURE_CREDENTIALS, use the following command: az ad sp create-for-rbac --name <sp name> --role contributor --scopes /subscriptions/<subscription>/resourceGroups/<resource group>/providers/Microsoft.Web/sites/<app> --json-auth
Copy the json output to the AZURE_CREDENTIALS environment variable as a github secret.
Step 4 - For the github action, change the webapp name in the yml file

push to prod
For alixia