# Площадка для работы с ЦФА

Площадка для выпуска, обращения и погашения цифровых финансовых активов (ЦФА) с использованием технологии распределенного реестра.

### Запуск приложения
```
./gradlew clean deployNodes
./build/nodes/runnodes --headless
./gradlew runPartyAServer
./gradlew runPartyBServer
./gradlew runPartyCServer
./gradlew runPartyDServer
```