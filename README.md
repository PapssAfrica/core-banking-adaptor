# core-banking-adaptor
This adaptor implements the operations needed to facilitate Outbound and Inbound payments via PAPSS.

There are 5 operations to be implemented by the iIntegrator namely:
##1. Authenticate 
##2. NameInquiry
##3. Credit Transfer(Debit Push,Credit Push)
##4. Credit Return (Recall)
##5. Message Signing

As an Integrator you role is to map the above operations to the internal format used by the banks core banking system.
Some banks have these API in one service some of them split the API so you will need to do some work to update the configuration to match the banks internal setup.

There is a package called **za.co.quadrantsystems.core.dto** this is where you will place the internal bank DTO's;
Note Please do not make any changes to the other DTO's otherwise advised so by papss team as these are consumed by the portal.


There are 2 levels of security
1. The application needs to be deployed on the VPN encryption domain, i.e the server that the bank will provision for your to deploy the adaptor needs to be accesable via the encryption domain.

2. Digital Certificate as configured by the papssPublicStoreFile configuration on the application.yml files, these certificates are used to sign and verify requests coming from papss to ensure no other person can call the adaptor.


The application is a spring boot application which can be deployed locally using java -jar or via docker if the bank supports it.


The application comes with swagger configured on [swagger url](http://localhost:8080/swagger-ui/index.html) this should allow you to view the API locally

There are 2 JKS files for testing the application namely **client.jks** and **papss-portal-backend_public_keystore.p12** these will be shared with you securely.
