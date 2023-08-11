package de.msg.javatraining.donationmanager.persistence.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum PermissionEnum {
    PERMISSION_MANAGEMENT(1,"PERMISSION_MANAGEMENT"),
    USER_MANAGEMENT(2, "USER_MANAGEMENT"),
    CAMP_MANAGEMENT(3,"CAMP_MANAGEMENT"),
    BENEF_MANAGEMENT(4, "BENEF_MANAGEMENT"),
    DONATION_MANAGEMENT(5, "DONATION_MANAGEMENT"),
    DONATION_APPROVE(6, "DONATION_APPROVE"),
    DONATION_REPORTING(7, "DONATION_REPORTING"),
    CAMP_REPORTING(8, "CAMP_REPORTING"),
    CAMP_IMPORT(9, "CAMP_IMPORT"),
    CAMP_REPORT_RESTRICTED(10, "CAMP_REPORT_RESTRICTED");

    private int id;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //INSERT INTO ROLE_PERMISSION (idRole, idPermission) values (2,PermissionEnum.PERMISSION_MANAGEMENT.getId())

    public static PermissionEnum getById(int id) {
        for (PermissionEnum permission : PermissionEnum.values()) {
            if (permission.getId() == id) {
                return permission;
            }
        }
        return null; // No permission found with the given ID
    }

}

