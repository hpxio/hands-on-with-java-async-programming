# Use Case
The server has huge list of transactions organized under each store.
So, for example each store for a given date range can have `n` transactions .
Where `n` can be `1 < n < 1000`. Now, the client services have a need to compile
a list of all transactions happened in a given date range grouped by each merchant 
and grouped by each store, store & merchant can be given in the request by clients.
The stores are classified under merchants such that each store belongs to one and only one
merchant. While, one merchant can have many stores under them.

### Transaction Configuration
```json
[
  '{{repeat(500)}}',
  {
    id:'{{index(10001)}}',
    storeId:'{{integer(5000, 5010)}}',
    amount:'{{floating(10, 20, 2, "0,0.00")}}',
    timestamp:'{{date(new Date(2024, 0, 1), new Date(), "YYYYMMddhhmmss")}}',
    status:'{{random("success", "failed")}}'
  }
]
```

### Store Configuration
```json
[
  '{{repeat(11)}}',
  {
    id:'{{index(5000)}}',
    name:'CodeName-{{company()}}',
    venue:'{{street()}} Store',
    merchantId:'{{random(1001, 2001, 3001, 4001)}}'
  }
]
```

### Merchant Configuration
```json
[
  '{{repeat(4)}}',
  {
    id:'{{random(1001, 2001, 3001, 4001)}}',
    uid:'{{index(43788119)}}',
    city:'{{city()}}',
    state:'{{state()}}',
    country:'{{country()}}'
  }
]
```

> Used in -> https://json-generator.com/#