LEIN = ./lein

$(LEIN):
	curl -s -o $(LEIN) https://raw.github.com/technomancy/leiningen/stable/bin/lein
	chmod +x $(LEIN)

build: $(LEIN)
	$(LEIN) uberjar
