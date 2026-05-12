package co.books.api.common.jpa;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.BasicBinder;
import org.hibernate.type.descriptor.jdbc.BasicExtractor;
import org.hibernate.type.descriptor.jdbc.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * PostgreSQL 사용자 정의 ENUM(order_status, payment_status, pay_method) 컬럼에
 * "문자열을 type-unknown 으로 전달"하기 위한 커스텀 JdbcType.
 *
 * <p>일반적인 VARCHAR 바인딩({@code setString})은 드라이버 설정에 따라
 * Oid.VARCHAR 로 전송되어 PostgreSQL 이 ENUM 컬럼과 캐스트하지 못한다.
 * 본 타입은 {@code setObject(idx, str, Types.OTHER)} 로 바인딩하여
 * 파라미터를 type-unknown 으로 전송하고, PG 가 컬럼 타입으로 자동 캐스트하도록 한다.</p>
 *
 * <p>AttributeConverter 와 함께 사용하면, 엔티티의 Java enum ↔ DB 문자열 변환은
 * 컨버터가 담당하고, 본 타입은 JDBC 바인딩만 담당한다.</p>
 */
public class PgUnknownStringJdbcType implements JdbcType {

    public static final PgUnknownStringJdbcType INSTANCE = new PgUnknownStringJdbcType();

    @Override
    public int getJdbcTypeCode() {
        return Types.OTHER;
    }

    @Override
    public int getDdlTypeCode() {
        return Types.VARCHAR;
    }

    @Override
    public String getFriendlyName() {
        return "PG_UNKNOWN_STRING";
    }

    @Override
    public <X> ValueBinder<X> getBinder(JavaType<X> javaType) {
        return new BasicBinder<>(javaType, this) {
            @Override
            protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
                String s = javaType.unwrap(value, String.class, options);
                st.setObject(index, s, Types.OTHER);
            }

            @Override
            protected void doBind(CallableStatement st, X value, String name, WrapperOptions options) throws SQLException {
                String s = javaType.unwrap(value, String.class, options);
                st.setObject(name, s, Types.OTHER);
            }
        };
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(JavaType<X> javaType) {
        return new BasicExtractor<>(javaType, this) {
            @Override
            protected X doExtract(ResultSet rs, int paramIndex, WrapperOptions options) throws SQLException {
                return javaType.wrap(rs.getString(paramIndex), options);
            }

            @Override
            protected X doExtract(CallableStatement st, int paramIndex, WrapperOptions options) throws SQLException {
                return javaType.wrap(st.getString(paramIndex), options);
            }

            @Override
            protected X doExtract(CallableStatement st, String name, WrapperOptions options) throws SQLException {
                return javaType.wrap(st.getString(name), options);
            }
        };
    }
}
