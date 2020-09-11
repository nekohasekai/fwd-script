FROM docker.pkg.github.com/nekohasekai/ktlib/td-base:latest

WORKDIR /root

ADD bot/target/td-group-bot.jar .

ENTRYPOINT java -jar td-group-bot.jar