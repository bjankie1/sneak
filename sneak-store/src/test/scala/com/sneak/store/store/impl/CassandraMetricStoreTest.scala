package com.sneak.store.store.impl

import org.specs2._
import org.specs2.mock.Mockito
import com.sneak.thrift.Message
import com.datastax.driver.core._
import com.sneak.store.util.ConfigurationLoader


class CassandraMetricStoreTest extends mutable.Specification
  with Mockito {


  "Cassandra store" should {
    "connect to Cassandra cluster" in {
      //given
      val config = ConfigurationLoader.load()
      val factory = mock[CassandraClusterBuilder]
      val cluster = mock[Cluster]
      val session = mock[Session]
      val stmt = mock[PreparedStatement]
      val columnDefinitions = mock[ColumnDefinitions]

      factory.build returns cluster
      cluster.connect() returns session
      session.prepare(anyString) returns(stmt)
      stmt.getVariables returns columnDefinitions
      columnDefinitions.size() returns 7
      columnDefinitions.getType(anyInt) returns
        DataType.uuid() thenReturn
        DataType.timestamp() thenReturns
        DataType.varchar() thenReturns
        DataType.cdouble() thenReturns
        DataType.varchar() thenReturns
        DataType.varchar() thenReturns
        DataType.map(DataType.varchar(), DataType.varchar())

      val store = new CassandraMetricStore(config, cluster)
      val message = Message(1l, "test", 1, "local", "testap", Map("key" -> "value"))

      store storeMetric  message

      there was one(cluster).connect()
      there was one(factory).build
      there was one(session).prepare(anyString)
      there was one(session).executeAsync(any[BoundStatement])
    }

  }


}
