ClickHouse C/S Native Protocol
===

## Connection

```mermaid
sequenceDiagram
Client -> Server: Open socket Connection
Server --> Client: Ok, got a new client connection
Note right of Server: Connection established
Client -> Server: Send Hello Request
Server --> Client: Hello response
Note left of Client: I got server infos
```

## Request

- There are many kinds of requests/response, the above `hello` is one of them.

- You can find all the request/response type in `com.github.housepower.jdbc.protocol` package.

## Query

After the connection established and hello request/response, we can send plain sql strings to query the data. 

```mermaid
sequenceDiagram
Client -> Server: Send DataRequest Request
Note right of Server: Oh, a new query just comes, <br/> I will handle that query.
Server --> Client: DataResponse
Note left of Client: I got response data now
Note left of Client: I will deserialize them to the ResultSets.
```

## Insert

The plain query which send sql literal to the server, but it's not efficient for batch inserts. ClickHouse provides another type of data request for batch inserts that we can send blocks to the server directly.

```mermaid
sequenceDiagram
Client -> Server: Send insert query to the server (which called by PreparedStatement)
Note right of Server: Oh, a new prepare insert just comes,<br/>I'll look at the table schemas.
Server --> Client: DataResponse (Empty Block, <br/> which is also called sampleBlock)
Note right of Server: State: Waiting for inserts.
Note left of Client: I got a block now, <br/> and I know the names and types of this table.
Note left of Client: Write the data to the blocks  <br/> (when we can `setObject` in JDBC)
Client -> Server: send a large block by dataRequest
Note right of Server: A Block just comes,  <br/> I'll insert them to the table
Client -> Server: send a empty block to end the inserts
Note right of Server: A empty block just comes,  <br/> which means the client finish the inserts.
Note right of Server: State: Idle.
```

 

