#!/bin/sh
curl -XPUT $ELASTIC_HOST/picture
curl -XPUT $ELASTIC_HOST/picture/_mapping/info -d @mapping/info.json
curl -XPUT $ELASTIC_HOST/picture/_mapping/file -d @mapping/file.json
