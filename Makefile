deploy:
	lein with-profile prod uberjar
	scp -r supervisor.conf $(HOST):/etc/supervisor/conf.d/coinmarketcap-feed.conf
	scp -r target/uberjar/coinmarketcap-feed.jar $(HOST):/var/www/coinmarketcap-feed.jar
	ssh $(HOST) sudo supervisorctl reread
	ssh $(HOST) sudo supervisorctl restart coinmarketcap-feed
