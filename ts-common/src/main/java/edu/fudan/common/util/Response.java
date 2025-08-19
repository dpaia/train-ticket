package edu.fudan.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

/**
 * @author fdse
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Response<T> {

    /**
     * 1 true, 0 false
     */
    Integer status;

    String msg;
    T data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Response<?> response = (Response<?>) o;
        return Objects.equals(status, response.status) &&
               Objects.equals(msg, response.msg) &&
               Objects.equals(data, response.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, msg, data);
    }
}
