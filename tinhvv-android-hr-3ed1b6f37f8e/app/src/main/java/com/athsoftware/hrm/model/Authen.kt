package com.athsoftware.hrm.model
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by tinhvv on 11/10/18.
 */
data class User(
        @SerializedName("ApplicationId")
        var applicationId : String? = null,
        @SerializedName("Pwd")
        var pwd : String? = null,
        @SerializedName("Active")
        var active : Boolean? = null,
        @SerializedName("Roles")
        var roles : List<Role>? = null,
        @SerializedName("Rights")
        var rights : String? = null,
        var newPwd : String? = null,
        var isExpire : Boolean? = null,
        @SerializedName("UserId")
        var userId : String? = null,
        @SerializedName("UserName")
        var userName : String? = null,
        @SerializedName("FullName")
        var fullName : String? = null,
        @SerializedName("Position")
        var position : String? = null,
        @SerializedName("Mobile")
        var mobile : String? = null,
        @SerializedName("StaffCode")
        var staffCode : String? = null,
        @SerializedName("StaffImg")
        var staffImg: String? = null
) : Serializable

data class Role (
        @SerializedName("RoleId")
        var roleId: String? = null,
        @SerializedName("RoleCode")
        var roleCode: String? = null,
        @SerializedName("RoleName")
        var roleName: String? = null
)