package com.powerboot.request.user;

import com.powerboot.request.BaseRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

/**
 * Create  2020 - 10 - 29 5:01 下午
 */
@ApiModel(value = "改昵称入参")
public class RenameRequest extends BaseRequest {

    @ApiModelProperty(value = "昵称")
    @NotEmpty(message = "please enter your nike name")
    @Length(max = 45)
    private String nikeName;

    public String getNikeName() {
        return nikeName;
    }

    public void setNikeName(String nikeName) {
        this.nikeName = nikeName;
    }
}
