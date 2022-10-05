package com.internship.rushhour.infrastructure.fieldaccess;

import com.internship.rushhour.domain.account.entity.Account;
import com.internship.rushhour.domain.activity.entity.Activity;
import com.internship.rushhour.domain.appointment.entity.Appointment;
import com.internship.rushhour.domain.client.entity.Client;
import com.internship.rushhour.domain.employee.entity.Employee;
import com.internship.rushhour.domain.provider.entity.Provider;
import com.internship.rushhour.domain.role.models.Roles;
import com.internship.rushhour.infrastructure.exceptions.AccessingLockedFieldException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EntityFieldPermissionFactory {
    private static Map<String, Map<String, Boolean>> permissions = new HashMap<>();
    private static Map<String, Boolean> lockedForAdmin = new HashMap<>();
    private static Map<String, Boolean> lockedForEmployee = new HashMap<>();
    private static Map<String, Boolean> lockedForClient = new HashMap<>();

    private static final String EMPLOYEE = Employee.class.getSimpleName();
    private static final String ACTIVITY = Activity.class.getSimpleName();
    private static final String APPOINTMENT = Appointment.class.getSimpleName();
    private static final String CLIENT = Client.class.getSimpleName();
    private static final String ACCOUNT = Account.class.getSimpleName();
    private static final String PROVIDER = Provider.class.getSimpleName();

    private static Logger logger = LoggerFactory.getLogger(EntityFieldPermissionFactory.class);

    static {
        lockForAll(ACCOUNT+"email", true);

        lockForEmployee(ACCOUNT+"role", true);
        lockForClient(ACCOUNT+"role", true);

        lockForAll(ACTIVITY+"provider", true);
        lockForEmployee(ACTIVITY+"employees", true);

        lockForEmployee(APPOINTMENT+"employee", true);
        lockForClient(APPOINTMENT+"employee", true);
        lockForClient(APPOINTMENT+"client", true);
        lockForClient(APPOINTMENT+"price", true);

        lockForAll(EMPLOYEE+"provider", true);

        // This is locked because it's used in security validation methods
        lockForAdmin(PROVIDER+"businessDomain", true);

        initializePermissions();
    }

    public static boolean isLocked(String role, String field){
        if ( !permissions.containsKey(role)) return false;
        if ( permissions.get(role).containsKey(field) && permissions.get(role).get(field)==true ){
           throw new AccessingLockedFieldException(role, field);
        }
        else return false;
    }

    private static void lockForAll(String field, boolean throwException){
        lockedForAdmin.put(field, throwException);
        lockedForEmployee.put(field, throwException);
        lockedForClient.put(field, throwException);
    }

    private static void lockForAdmin(String field, boolean throwException){
        lockedForAdmin.put(field, throwException);
    }

    private static void lockForEmployee(String field, boolean throwException){
        lockedForEmployee.put(field, throwException);
    }

    private static void lockForClient(String field, boolean throwException){
        lockedForClient.put(field, throwException);
    }

    private static void initializePermissions(){
        permissions.put(Roles.ROLE_EMPLOYEE.name(), lockedForEmployee);
        permissions.put(Roles.ROLE_PROVIDER_ADMINISTRATOR.name(), lockedForAdmin);
        permissions.put(Roles.ROLE_CLIENT.name(), lockedForClient);
    }
}
