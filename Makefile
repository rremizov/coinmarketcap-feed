build:
	lein with-profile prod uberjar

docker_build: build
	docker build -t coinmarketcap-feed -t registry.gitlab.com/rremizov/coinmarketcap-feed .

docker_push: docker_build
	docker push registry.gitlab.com/rremizov/coinmarketcap-feed
