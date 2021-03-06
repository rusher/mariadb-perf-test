package org.test;

import org.openjdk.jmh.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.util.Arrays;

@State(Scope.Benchmark)
public class JmhPerfTest {

    @State(Scope.Thread)
    public static class MyState {

        public int counter = 0;
        protected Connection sharedConnectionRewrite;
        protected Connection sharedConnection;
        protected Connection sharedConnectionNoCache;
        protected Connection sharedConnectionText;
        protected Connection sharedFailoverConnection;

        protected Statement sharedStatementRewrite;
        protected Statement sharedStatement;
        protected Statement sharedStatementFailover;

        @Setup
        public void doSetup() throws SQLException {
            sharedConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/testj?user=root&useServerPrepStmts=true");
            sharedConnectionNoCache = DriverManager.getConnection("jdbc:mysql://localhost:3306/testj?user=root&useServerPrepStmts=true&cacheCallableStmts=false&cachePrepStmts=false");
            sharedConnectionText = DriverManager.getConnection("jdbc:mysql://localhost:3306/testj?user=root&useServerPrepStmts=false");
            sharedConnectionRewrite = DriverManager.getConnection("jdbc:mysql://localhost:3306/testj?user=root&rewriteBatchedStatements=true");
            try {
                sharedFailoverConnection = DriverManager.getConnection("jdbc:mysql:replication://localhost:3306,localhost:3306/testj?user=root&useServerPrepStmts=true&validConnectionTimeout=0");
                sharedStatementFailover = sharedFailoverConnection.createStatement();
            } catch (SQLException sqlException) {
                sharedFailoverConnection = null;
                sharedStatementFailover = null;
            }
            sharedStatement = sharedConnection.createStatement();

            //use black hole engine. so test are not stored and to avoid server disk access
            //if "java.sql.SQLSyntaxErrorException: Unknown storage engine 'BLACKHOLE'". restart database
            sharedStatement.execute("INSTALL SONAME 'ha_blackhole'");

            sharedStatement.execute("DROP TABLE IF EXISTS PerfTextQuery ");
            sharedStatement.execute("CREATE TABLE PerfTextQuery(charValue VARCHAR(100) NOT NULL) ENGINE = BLACKHOLE");

            sharedStatement.execute("DROP TABLE IF EXISTS PerfTextQueryBlob ");
            sharedStatement.execute("CREATE TABLE PerfTextQueryBlob(blobValue LONGBLOB NOT NULL) ENGINE = BLACKHOLE");

            sharedStatement.execute("DROP TABLE IF EXISTS PerfReadQuery ");
            sharedStatement.execute("CREATE TABLE PerfReadQuery(id int, charValue VARCHAR(100) NOT NULL )");

            sharedStatement.execute("DROP TABLE IF EXISTS PerfReadQueryBig ");
            sharedStatement.execute("CREATE TABLE PerfReadQueryBig(charValue VARCHAR(5000), charValue2 VARCHAR(5000) NOT NULL)");

            sharedStatement.execute("DROP PROCEDURE IF EXISTS withResultSet ");
            sharedStatement.execute("CREATE PROCEDURE withResultSet(a int) begin select a; end");

            sharedStatement.execute("DROP PROCEDURE IF EXISTS inoutParam ");
            sharedStatement.execute("CREATE PROCEDURE inoutParam(INOUT p1 INT) begin set p1 = p1 + 1; end");

            sharedStatement.execute("DROP FUNCTION IF EXISTS testFunctionCall ");
            sharedStatement.execute("CREATE FUNCTION testFunctionCall(a float, b bigint, c int) RETURNS INT NO SQL \n"
                    + "BEGIN \n"
                    + "RETURN a; \n"
                    + "END");

            try {
                PreparedStatement preparedStatement = sharedConnectionRewrite.prepareStatement("INSERT INTO PerfReadQuery (id, charValue) values (?, ?)");
                for (int i = 0; i < 1000; i++) {
                    preparedStatement.setInt(1, i);
                    preparedStatement.setString(2, "abc" + (counter++) + "'");
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            } catch (Exception e) {
                PreparedStatement preparedStatement = sharedConnection.prepareStatement("INSERT INTO PerfReadQuery (id, charValue) values (?, ?)");
                for (int i = 0; i < 1000; i++) {
                    preparedStatement.setInt(1, i);
                    preparedStatement.setString(2, "abc" + (counter++) + "'");
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }

            byte[] arr = new byte[5000];
            for (int i = 0; i < 5000; i++) {
                arr[i] = (byte)(i % 128);
            }
            String data = new String(arr);
            try {
                PreparedStatement preparedStatement2 = sharedConnectionRewrite.prepareStatement("INSERT INTO PerfReadQueryBig (charValue, charValue2) values (?, ?)");
                for (int i = 0; i < 1000; i++) {
                    preparedStatement2.setString(1, data);
                    preparedStatement2.setString(2, data);
                    preparedStatement2.addBatch();
                }
                preparedStatement2.executeBatch();
            } catch (Exception e) {
                PreparedStatement preparedStatement2 = sharedConnection.prepareStatement("INSERT INTO PerfReadQueryBig (charValue, charValue2) values (?, ?)");
                for (int i = 0; i < 1000; i++) {
                    preparedStatement2.setString(1, data);
                    preparedStatement2.setString(2, data);
                    preparedStatement2.addBatch();
                }
                preparedStatement2.executeBatch();
            }


            sharedStatementRewrite = sharedConnectionRewrite.createStatement();
        }

        @TearDown(Level.Trial)
        public void doTearDown() throws SQLException {
            sharedConnection.close();
            sharedConnectionRewrite.close();
            sharedConnectionText.close();
            if (sharedFailoverConnection != null) {
                sharedFailoverConnection.close();
            }
        }
    }

    @Benchmark
    public void executeOneInsert(MyState state) throws Throwable {
        state.sharedStatement.execute("INSERT INTO PerfTextQuery (charValue) values ('abc" + (state.counter++) + "')");
    }

    @Benchmark
    public void executeOneInsertFailover(MyState state) throws Throwable {
        state.sharedStatementFailover.execute("INSERT INTO PerfTextQuery (charValue) values ('abc" + (state.counter++) + "')");
    }

    @Benchmark
    public void executeOneInsertBinaryCache(MyState state) throws Throwable {
        PreparedStatement preparedStatement = state.sharedConnection.prepareStatement("INSERT INTO PerfTextQuery (charValue) values (?)");
        preparedStatement.setString(1, "abc" + (state.counter++) + "'");
        preparedStatement.execute();
    }

    @Benchmark
    public void executeOneInsertBinaryNoCache(MyState state) throws Throwable {
        PreparedStatement preparedStatement = state.sharedConnectionNoCache.prepareStatement("INSERT INTO PerfTextQuery (charValue) values (?)");
        preparedStatement.setString(1, "abc" + (state.counter++) + "'");
        preparedStatement.execute();
    }


    @Benchmark
    public void executeOneInsertBinaryFailoverCache(MyState state) throws Throwable {
        PreparedStatement preparedStatement = state.sharedFailoverConnection.prepareStatement("INSERT INTO PerfTextQuery (charValue) values (?)");
        preparedStatement.setString(1, "abc" + (state.counter++) + "'");
        preparedStatement.execute();
    }

    @Benchmark
    public void executeDo1(MyState state) throws Throwable {
        state.sharedStatement.executeQuery("DO 1");
    }

    @Benchmark
    public void executeSelect1(MyState state) throws Throwable {
        ResultSet rs = state.sharedStatementFailover.executeQuery("SELECT * FROM PerfReadQuery where id = 0");
        rs.next();
        rs.getString(1);
    }

    @Benchmark
    public void executeSelect1Name(MyState state) throws Throwable {
        ResultSet rs = state.sharedStatementFailover.executeQuery("SELECT * FROM PerfReadQuery where id = 0");
        rs.next();
        rs.getString("charValue");
    }

    @Benchmark
    public void executeSelect1Failover(MyState state) throws Throwable {
        state.sharedStatementFailover.executeQuery("SELECT PerfReadQuery");
    }

    @Benchmark()
    public void executeSelect1000rows(MyState state) throws Throwable {
        ResultSet rs = state.sharedStatement.executeQuery("SELECT * FROM PerfReadQuery");
        while (rs.next()) {
            rs.getString(1);
        }
    }

    @Benchmark()
    public void executeSelectBig1000rows(MyState state) throws Throwable {
        ResultSet rs = state.sharedStatement.executeQuery("SELECT * FROM PerfReadQueryBig");
        while (rs.next()) {
            rs.getString(1);
            rs.getString(2);
        }
    }

    @Benchmark
    public void callableStatementWithInParameter(MyState state) throws Throwable {
        CallableStatement stmt = state.sharedConnection.prepareCall("{call withResultSet(?)}");
        stmt.setInt(1, 1);
        ResultSet rs = stmt.executeQuery();
    }

    @Benchmark
    public void callableStatementWithOutParameter(MyState state) throws Throwable {
        CallableStatement storedProc = state.sharedConnection.prepareCall("{call inOutParam(?)}");
        storedProc.setInt(1, 1);
        storedProc.registerOutParameter(1, Types.INTEGER);
        storedProc.execute();
        storedProc.getString(1);
    }

    @Benchmark
    public void callableStatementFunction(MyState state) throws Throwable {
        CallableStatement callableStatement = state.sharedConnection.prepareCall("{? = CALL testFunctionCall(?,?,?)}");
        callableStatement.registerOutParameter(1, Types.INTEGER);
        callableStatement.setFloat(2, 2);
        callableStatement.setInt(3, 1);
        callableStatement.setInt(4, 1);
        callableStatement.execute();
    }

    @Benchmark
    public void executeBatch1000Insert(MyState state) throws Throwable {
        PreparedStatement preparedStatement = state.sharedConnectionText.prepareStatement("INSERT INTO PerfTextQuery (charValue) values (?)");
        for (int i = 0; i < 1000; i++) {
            preparedStatement.setString(1, "abc" + (state.counter++) + "'");
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
    }


    @Benchmark
    public void executeBatch1000InsertBinaryCache(MyState state) throws Throwable {
        PreparedStatement preparedStatement = state.sharedConnection.prepareStatement("INSERT INTO PerfTextQuery (charValue) values (?)");
        for (int i = 0; i < 1000; i++) {
            preparedStatement.setString(1, "abc" + (state.counter++) + "'");
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
    }


    @Benchmark
    public void executeBatch1000InsertBinaryNoCache(MyState state) throws Throwable {
        PreparedStatement preparedStatement = state.sharedConnectionNoCache.prepareStatement("INSERT INTO PerfTextQuery (charValue) values (?)");
        for (int i = 0; i < 1000; i++) {
            preparedStatement.setString(1, "abc" + (state.counter++) + "'");
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
    }

    @Benchmark
    public void executeBatch1000InsertRewrite(MyState state) throws Throwable {
        PreparedStatement preparedStatement = state.sharedConnectionRewrite.prepareStatement("INSERT INTO PerfTextQuery (charValue) values (?)");
        for (int i = 0; i < 10000; i++) {
            preparedStatement.setString(1, "abc" + (state.counter++) + "'");
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
    }


}
