package org.dimensinfin.eveonline.neocom.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SBRawStatement extends RawStatement {
    private PreparedStatement prepStmt;
    private ResultSet cursor;

    public SBRawStatement(final Connection privateConnection, final String query, final String[] parameters) throws
            SQLException {
        if (null != privateConnection) {
            prepStmt = privateConnection.prepareStatement(query);
            for (int i = 0; i < parameters.length; i++) {
                prepStmt.setString(i + 1, parameters[i]);
            }
            cursor = prepStmt.executeQuery();
            if (null == cursor)
                throw new SQLException("Invalid statement when processing query: " + query);
        } else
            throw new SQLException("No valid connection to database to create statement. {}", query);
    }

    @Override
    public boolean moveToFirst() {
        try {
            return cursor.first();
        } catch (SQLException sqle) {
            return false;
        }
    }

    @Override
    public boolean moveToLast() {
        try {
            return cursor.last();
        } catch (SQLException sqle) {
            return false;
        }
    }

    @Override
    public boolean moveToNext() {
        try {
            return cursor.next();
        } catch (SQLException sqle) {
            return false;
        }
    }

    @Override
    public boolean isFirst() {
        try {
            return cursor.isFirst();
        } catch (SQLException sqle) {
            return false;
        }
    }

    @Override
    public boolean isLast() {
        try {
            return cursor.isLast();
        } catch (SQLException sqle) {
            return false;
        }
    }

    @Override
    public String getString(int colindex) {
        try {
            return cursor.getString(colindex);
        } catch (SQLException sqle) {
            return "";
        }
    }

    @Override
    public short getShort(int colindex) {
        try {
            return cursor.getShort(colindex);
        } catch (SQLException sqle) {
            return 0;
        }
    }

    @Override
    public int getInt(int colindex) {
        try {
            return cursor.getInt(colindex);
        } catch (SQLException sqle) {
            return 0;
        }
    }

    @Override
    public long getLong(int colindex) {
        try {
            return cursor.getLong(colindex);
        } catch (SQLException sqle) {
            return 0;
        }
    }

    @Override
    public float getFloat(int colindex) {
        try {
            return cursor.getFloat(colindex);
        } catch (SQLException sqle) {
            return 0;
        }
    }

    @Override
    public double getDouble(int colindex) {
        try {
            return cursor.getDouble(colindex);
        } catch (SQLException sqle) {
            return 0;
        }
    }

    public void close() {
        try {
            if (null != cursor) cursor.close();
            if (null != prepStmt) prepStmt.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}
