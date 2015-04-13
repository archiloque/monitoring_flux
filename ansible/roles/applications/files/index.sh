#!/bin/bash

curl -XPOST 'http://localhost:9200/monitoring' -d '{
  "settings": {
    "number_of_shards": 1
  },
  "mappings": {
    "cep_to_elasticsearch": {
      "properties": {
        "correlation_id": {
          "type": "string",
          "index": "not_analyzed"
        },
        "module_type": {
          "type": "string",
          "index": "not_analyzed",
          "doc_values": true
        },
        "module_id": {
          "type": "string",
          "index": "not_analyzed"
        },
        "endpoint": {
          "type": "string",
          "index": "not_analyzed",
          "doc_values": true
        },
        "message_type": {
          "type": "string",
          "index": "not_analyzed",
          "doc_values": true
        },
        "timestamp": {
          "type": "date",
          "format": "dateOptionalTime"
        },
        "begin_timestamp": {
          "type": "date",
          "format": "dateOptionalTime"
        },
        "end_timestamp": {
          "type": "date",
          "format": "dateOptionalTime"
        },
        "elapsed_time": {
          "type": "double"
        },
        "service_params": {
          "type": "object"
        },
        "headers": {
          "type": "object"
        },
        "result": {
          "type": "object"
        }
      }
    },
    "throttling_violation": {
      "properties": {
        "alert_type": {
          "type": "string",
          "index": "not_analyzed"
        },
        "timestamp": {
          "type": "date",
          "format": "dateOptionalTime"
        },
        "avg_cnt": {
          "type": "double"

        },
        "correlation_id": {
          "type": "string",
          "index": "not_analyzed"
        }
      }
    },
    "unit_sla_violation": {
      "properties": {
        "alert_type": {
          "type": "string",
          "index": "not_analyzed"
        },
        "timestamp": {
          "type": "date",
          "format": "dateOptionalTime"
        },
        "module": {
          "type": "string",
          "index": "not_analyzed"
        },
        "time": {
          "type": "double"
        }
      }
    },
    "global_sla_violation": {
      "properties": {
        "alert_type": {
          "type": "string",
          "index": "not_analyzed"
        },
        "timestamp": {
          "type": "date",
          "format": "dateOptionalTime"
        },
        "correlation_id": {
          "type": "string",
          "index": "not_analyzed"
        },
        "count": {
          "type": "double"
        }
      }
    }
  }
}'
