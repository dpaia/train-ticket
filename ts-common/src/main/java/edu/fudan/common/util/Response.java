package edu.fudan.common.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author fdse
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel(description = "Standard response envelope")
public class Response<T> {
    /**
     * 1 true, 0 false
     */
    @ApiModelProperty(value = "1 = success, 0 = failure", example = "1")
    Integer status;

    @ApiModelProperty(value = "Human-readable message", example = "SUCCESS")
    String msg;

    @ApiModelProperty(value = "Payload returned by the endpoint")
    T data;
}
