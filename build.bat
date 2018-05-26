call mvn compile -DskipTests -f ingameads-config-server    
call mvn compile -DskipTests -f ingameads-image-provider    
call mvn compile -DskipTests -f ingameads-image-provider-gateway    
call mvn compile -DskipTests -f ingameads-service-registry    
call mvn compile -DskipTests -f ingameads-stats-server-gateway    
call mvn compile -DskipTests -f ingameads-ui

call mvn package -DskipTests -f ingameads-config-server    
call mvn package -DskipTests -f ingameads-image-provider    
call mvn package -DskipTests -f ingameads-image-provider-gateway    
call mvn package -DskipTests -f ingameads-service-registry    
call mvn package -DskipTests -f ingameads-stats-server-gateway    
call mvn package -DskipTests -f ingameads-ui