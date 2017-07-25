BINARY=./go-cloud
ENTRY=./
PREFIX=cargo.caicloud.io/clever
ifndef TAGS
TAGS=dev
endif

default: compile-dev

compile-dev:
	go build -v -o $(BINARY) $(ENTRY)

clean:
	rm -rf $(BINARY)

compile-linux:
	docker run --rm \
	-v `pwd`:/go/src/github.com/ustcliao/go-cloud \
	-e GOPATH=/go \
	-e GOOS=linux \
	-e GOARCH=amd64 \
	cargo.caicloud.io/caicloud/golang:1.7 \
	sh -c "cd /go/src/github.com/ustcliao/go-cloud && go build -v -o $(BINARY) $(ENTRY)"

build:
	docker build -t $(PREFIX)/go-cloud:$(TAGS) .

push:
	docker push $(PREFIX)/go-cloud:$(TAGS)

publish: compile-linux build push
