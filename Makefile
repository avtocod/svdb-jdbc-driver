.PHONY: build proto test cover publish

PROTO_PATH = proto
PROTO_SRC = $(PROTO_PATH)/**/*.proto
ANTLR_PATH = antlr
ANTLR_SDQL_SRC = $(ANTLR_PATH)/sdql.g4
GENERATED_TRG_KT = kt/model/src/main/kotlin
GENERATED_TRG_J = kt/model/src/main/java
ANTLR_PACKAGE = codes.spectrum.svdb.sdql.parser

## выполняет все тесты
test:
	./gradlew test

publish:
	./gradlew publish

build-jdbc:
	./gradlew :jdbc:fatJar

proto: --init-dirs $(PROTO_SRC)
	PROTO=true ./gradlew generateProto

## создает временные директории, нужные для билда
--init-dirs:
	mkdir -p $(GENERATED_TRG_KT)
	mkdir -p $(GENERATED_TRG_KT)
	mkdir -p $(ANTLR_TRG_J)

get-binary: clear-binary ## скачать бинарные файлы
	docker cp $$(docker create avtocod/svdb-jdbc-test:0.0.1):/opt/svdb-srv - > svdb-srv
	mkdir "test_instance"
	mv svdb-srv ./test_instance/svdb-srv
	chmod +x ./test_instance/svdb-srv

clear-binary: ## почистить директорию с бинарными файлами
	rm -Rf ./test_instance