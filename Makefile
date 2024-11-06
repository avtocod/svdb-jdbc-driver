.PHONY: help test

PROTO_PATH = proto
PROTO_SRC = $(PROTO_PATH)/**/*.proto
ANTLR_PATH = antlr
ANTLR_SDQL_SRC = $(ANTLR_PATH)/sdql.g4
GENERATED_TRG_KT = kt/model/src/main/kotlin
GENERATED_TRG_J = kt/model/src/main/java
ANTLR_PACKAGE = codes.spectrum.svdb.sdql.parser

help: ## This help.
	@printf "\033[33m%s:\033[0m\n" 'Available commands'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  \033[32m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

test: ## выполняет все тесты
	@./gradlew test

publish: ## публикация
	./gradlew publish

build-jdbc: ## собрать билд
	./gradlew :jdbc:fatJar

proto: --init-dirs $(PROTO_SRC) ## генерация прото
	PROTO=true ./gradlew generateProto

## создает временные директории, нужные для билда
--init-dirs:
	mkdir -p $(GENERATED_TRG_KT)
	mkdir -p $(GENERATED_TRG_KT)
	mkdir -p $(ANTLR_TRG_J)

get-binary: clear-binary  ## скачать бинарные файлы
	@printf "\n\e[30;42m %s \033[0m\n\n" 'Download binary file'
	@mkdir "test_instance"
	@docker cp $$(docker create avtocod/svdb-jdbc-test:0.0.2):/opt/svdb-srv ./test_instance/svdb-srv

clear-binary: ## почистить директорию с бинарными файлами
	@rm -Rf ./test_instance
