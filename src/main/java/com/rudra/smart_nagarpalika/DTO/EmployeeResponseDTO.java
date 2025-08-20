    package com.rudra.smart_nagarpalika.DTO;

    import com.rudra.smart_nagarpalika.Model.EmployeeModel;
    import com.rudra.smart_nagarpalika.Model.WardsModel;
    import lombok.Builder;
    import lombok.Data;

    import java.util.List;
    import java.util.stream.Collectors;

    @Data

    public class EmployeeResponseDTO {
        private Long id;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String departmentName;
        private List<String> wardNames; // Multiple wards
        private String role;

        public EmployeeResponseDTO(EmployeeModel employee) {
            this.id = employee.getId();
            this.firstName = employee.getFirstName();
            this.lastName = employee.getLastName();
            this.phoneNumber = employee.getPhoneNumber();
            this.role = employee.getRole() != null ? employee.getRole().toString() : null;
            this.departmentName = employee.getDepartment() != null ? employee.getDepartment().getName() : null;
            this.wardNames = employee.getWards() != null
                    ? employee.getWards().stream().map(WardsModel::getName).collect(Collectors.toList())
                    : List.of();
        }


    }
