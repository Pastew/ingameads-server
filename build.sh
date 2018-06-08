mvn compile -DskipTests -f ingameads-config-server    
mvn compile -DskipTests -f ingameads-image-provider    
mvn compile -DskipTests -f ingameads-image-provider-gateway    
mvn compile -DskipTests -f ingameads-service-registry    
mvn compile -DskipTests -f ingameads-stats-server-gateway    
mvn compile -DskipTests -f ingameads-ui

mvn package -DskipTests -f ingameads-config-server    
mvn package -DskipTests -f ingameads-image-provider    
mvn package -DskipTests -f ingameads-image-provider-gateway    
mvn package -DskipTests -f ingameads-service-registry    
mvn package -DskipTests -f ingameads-stats-server-gateway 
mvn package -DskipTests -f ingameads-ui
