route: http://localhost:8083/connectors/ecommerce-mysql-cdc/config (put method)
```json
{
	"connector.class": "io.debezium.connector.mysql.MySqlConnector",
	"tasks.max": "1",
	"key.converter": "org.apache.kafka.connect.json.JsonConverter",
	"key.converter.schemas.enable": "true",
	"value.converter": "org.apache.kafka.connect.json.JsonConverter",
	"value.converter.schemas.enable": "true",
	"database.hostname": "mysql",
	"database.port": "3306",
	"database.user": "root",
	"database.password": "123456",
	"database.server.id": "10000",
	"database.server.name": "ecommerce-mysql",
	"database.allowPublicKeyRetrieval": "true",
	"database.whitelist": "ecommerce",
	"table.whitelist": "ecommerce.outbox",
	"database.history.kafka.bootstrap.servers": "kafka:9092",
	"database.history.kafka.topic": "ecommerce.dbhistory",
	"include.schema.changes": "false",
	"schema.enable": "false",
	"transforms": "route, filter",
  "transforms.route.type": "io.debezium.transforms.ContentBasedRouter",
  "transforms.route.language": "jsr223.groovy",
  "transforms.route.topic.expression": "value != null && value.after != null ? (value.op == 'd' || value.op == 'u' ? '' : value.after.aggregate_name + '-topic') : 'ecommerce-mysql.ecommerce.outbox'",
	"transforms.filter.type": "io.debezium.transforms.Filter",
	"transforms.filter.language": "jsr223.groovy",
	"transforms.filter.condition": "value != null && value.op != null && !(value.op == 'd' || value.op == 'u')"
}
```