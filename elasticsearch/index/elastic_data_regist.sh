#!/bin/sh
curl -XPOST $ELASTIC_HOST/picture/_bulk --data-binary "@data/pic_data"
curl -XPOST $ELASTIC_HOST/picture/_bulk --data-binary "@data/tmp_data"
