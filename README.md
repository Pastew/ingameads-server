# In game ads
This is server-side app for renting advert space inside game world.\
This application is an intermediary between video game owners and advertisers that are willing to pay for placing their advertisements inside the game world.\
Payment is fully covered in this app, I used PayPay for it.\
The entire system is a complete and working prototype with basic flow implemented and it is ready for new features development.\

## Technology used, some keywords:
* Java 8
* Spring Boot
* Microservice architecture
* Spring Cloud Config server - for remote configuration deployment
* Spring Netflix-Eureka - for microservices discovery
* In-game advertising
* Used SQL database for user related data
* Remote mongoDB database for statistics data (adverts views number)
* Remote image hosting - Cloudinary.

## Architecture
* Here's architecture picture.\
(TODO: translate to english)\
![Alt text](architecture.PNG?raw=true "Architecture")

* Screenshot from Eureka Server\
![Alt text](eureka.PNG?raw=true "eureka")

## User Interface
* UI was rendered with thmyeleaf.
* For CSS I used Google Materialize (materializecss.com) just because I didn't know it and I wanted to learn it.\

* Here's how home page looks like:\
![Alt text](UI.PNG?raw=true "UI")

* Game page\
Here advert provider can choose start and end day of advertising campaign, upload advert image and know how much it will cost him. He also can see stats of this game.\
![Alt text](game.PNG?raw=true "game")

* Offer page\
Once advert provider completed order, game developer needs to approve (or reject) it.\
![Alt text](offer.PNG?raw=true "offer")

* Payment\
Here's how payment works. Part of money goes to In-game-ads owner, part to PayPay and the rest to game developer. Numbers are configurable.\
![Alt text](payment.PNG?raw=true "payment")

When game developer accepts the offer - advert provider is notified about this and he can pay in advance for the advert campaign.\
![Alt text](paypal.PNG?raw=true "paypal")


## Client app
Example client game can be found here: https://github.com/Pastew/ingameads-client

## Why did I create this app?
The only purpose of creating this app was to learn how Spring Boot works, I don't plan to use it commercially. I also used it as my master's thesis.


## How to build this repo from scratch on Linux (tested on CentOS)
```
# install GIT
yum install git

# install Java 8
yum install java-1.8.0-openjdk-devel
export JAVA_HOME=/usr/lib/jvm/jre-1.8.0-openjdk.x86_64/

# install Maven
cd /opt
wget http://mirrors.sonic.net/apache/maven/maven-3/3.5.3/binaries/apache-maven-3.5.3-bin.zip
yum -y install unzip
unzip apache-maven-3.5.3-bin.zip
mv apache-maven-3.5.3 maven
rm -f apache-maven-3.5.3-bin.zip
echo "export PATH=/opt/maven/bin:${PATH}" > /etc/profile.d/maven.sh

# Clone repo
cd /root
git clone https://github.com/Pastew/ingameads-server
cd ingameads-server

# compile and buld .jar files
sh build.sh

# run microservices
nohup ./unix_run_config_server.sh &> unix_run_config_server.out&
nohup ./unix_run_eureka.sh &> unix_run_eureka.out&

nohup ./unix_run_image_provider-gateway.sh &> unix_run_image_provider-gateway.out&
nohup ./unix_run_image_provider.sh &> unix_run_image_provider.out&
nohup ./unix_run_stats.sh &> unix_run_stats.out&

nohup ./unix_run_ui.sh &> unix_run_ui.out&
```
