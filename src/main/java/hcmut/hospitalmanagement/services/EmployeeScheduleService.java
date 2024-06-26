package hcmut.hospitalmanagement.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import hcmut.hospitalmanagement.models.Employee;
import hcmut.hospitalmanagement.models.EmployeeSchedule;
import hcmut.hospitalmanagement.models.Patient;
import hcmut.hospitalmanagement.models.ResponseObject;
import hcmut.hospitalmanagement.repositories.EmployeeRepository;
import hcmut.hospitalmanagement.repositories.EmployeeScheduleRepository;
import hcmut.hospitalmanagement.repositories.PatientRepository;

@Service
public class EmployeeScheduleService {
    @Autowired
    private EmployeeScheduleRepository employeeScheduleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PatientRepository patientRepository;

    public ResponseEntity<ResponseObject> getAllEmployeeSchedule() {
        List<EmployeeSchedule> scheduleList = employeeScheduleRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK)
        .body(new ResponseObject("OK", "Querry employee schedule successfully", scheduleList));
    }

    public ResponseEntity<ResponseObject> getActiveEmployeeSchedule() {
        LocalDateTime currentTime = LocalDateTime.now();
        List<EmployeeSchedule> scheduleList = employeeScheduleRepository.findByStartTimeAfter(currentTime);
        return ResponseEntity.status(HttpStatus.OK)
        .body(new ResponseObject("OK", "Querry employee schedule successfully", scheduleList));
    }

    public ResponseEntity<ResponseObject> getScheduleBetweenTime(LocalDateTime time1, LocalDateTime time2) {
        List<EmployeeSchedule> scheduleList = employeeScheduleRepository.findByStartTimeBetweenOrderByStartTime(time1, time2);
        return ResponseEntity.status(HttpStatus.OK)
        .body(new ResponseObject("OK", "Querry employee schedule successfully", scheduleList));
    }

    public ResponseEntity<ResponseObject> getScheduleByEmployeeId(Long employeeId) {
        List<EmployeeSchedule> scheduleList = employeeScheduleRepository.findByEmployeeId(employeeId);
        return ResponseEntity.status(HttpStatus.OK)
        .body(new ResponseObject("OK", "Querry employee schedule successfully", scheduleList));
    }

    public ResponseEntity<ResponseObject> getActiveScheduleByEmployeeId(Long employeeId) {
        LocalDateTime currentTime = LocalDateTime.now();
        List<EmployeeSchedule> scheduleList = employeeScheduleRepository.findByEmployeeIdAndStartTimeAfter(employeeId, currentTime);
        return ResponseEntity.status(HttpStatus.OK)
        .body(new ResponseObject("OK", "Querry employee schedule successfully", scheduleList));
    }

    public ResponseEntity<ResponseObject> getScheduleByEmployeeIdBetweenTime(Long employeeId, LocalDateTime time1, LocalDateTime time2) {
        List<EmployeeSchedule> scheduleList = employeeScheduleRepository.findByEmployeeIdAndStartTimeBetweenOrderByStartTime(employeeId, time1, time2);
        return ResponseEntity.status(HttpStatus.OK)
        .body(new ResponseObject("OK", "Querry employee schedule successfully", scheduleList)); 
    }

    public ResponseEntity<ResponseObject> insertScheduleByEmployeeId(Long employeeId, EmployeeSchedule newEmployeeSchedule) {
        // Kiểm tra có start time chưa
        if (newEmployeeSchedule.getStartTime() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(new ResponseObject("Failed", "Start Time must not be null", null));
        }
        // Kiểm tra có end time chưa
        if (newEmployeeSchedule.getEndTime() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(new ResponseObject("Failed", "End Time must not be null", null));
        }
        // Kiểm tra có title chưa
        if (newEmployeeSchedule.getTitle() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(new ResponseObject("Failed", "Title must not be null", null));
        }
        // Nếu trong lịch có patient id thì kiểm tra xem bệnh nhân có tồn tại không
        if (newEmployeeSchedule.getPatientId() != null ) {
            Patient foundPatient = patientRepository.findById(newEmployeeSchedule.getPatientId()).orElse(null);
            if (foundPatient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject("Failed", "Patient id does not exists", null));
            }
            newEmployeeSchedule.setPatientFirstName(foundPatient.getInformation().getFirstName());
            newEmployeeSchedule.setPatientLastName(foundPatient.getInformation().getLastName());
        }
        // Kiểm tra có phòng chưa
        if (newEmployeeSchedule.getRoom() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(new ResponseObject("Failed", "Room must not be null", null));
        }
        // Kiểm tra employee có tồn tại không
        Employee foundEmployee = employeeRepository.findById(employeeId).orElse(null);
        if (foundEmployee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ResponseObject("Failed", "Cannot find any employee with id: " + employeeId, null));
        }
        newEmployeeSchedule.setEmployee(foundEmployee);
        return ResponseEntity.status(HttpStatus.OK)
        .body(new ResponseObject("OK", "Insert employee schedule successfully", 
                                employeeScheduleRepository.save(newEmployeeSchedule)));
    }

    public ResponseEntity<ResponseObject> updateEmployeeScheduleById(Long employeeScheduleId, Long employeeId,
    EmployeeSchedule updatedEmployeeSchedule) {
        // Kiểm tra xem lịch có tồn tại không
        if (!employeeScheduleRepository.existsById(employeeScheduleId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ResponseObject("Failed", "Cannot find any schedule with id: " + employeeScheduleId, null));
        }
        // Kiểm tra có start time chưa
        if (updatedEmployeeSchedule.getStartTime() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(new ResponseObject("Failed", "Start Time must not be null", null));
        }
        // Kiểm tra có end time chưa
        if (updatedEmployeeSchedule.getEndTime() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(new ResponseObject("Failed", "End Time must not be null", null));
        }
        // Kiểm tra có title chưa
        if (updatedEmployeeSchedule.getTitle() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(new ResponseObject("Failed", "Title must not be null", null));
        }
        // Kiểm tra xem có phòng chưa
        if (updatedEmployeeSchedule.getRoom() == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
            .body(new ResponseObject("Failed", "Room must not be null", null));
        }
        // Nếu trong lịch có patient id thì kiểm tra xem patient có tồn tại không
        if (updatedEmployeeSchedule.getPatientId() != null) {
            Patient foundPatient = patientRepository.findById(updatedEmployeeSchedule.getPatientId()).orElse(null);
            if (foundPatient == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ResponseObject("Failed", "Patient id does not exists", null));
            }
            updatedEmployeeSchedule.setPatientFirstName(foundPatient.getInformation().getFirstName());
            updatedEmployeeSchedule.setPatientLastName(foundPatient.getInformation().getLastName());
        }
        // Kiểm tra employee có tồn tại không
        Employee foundEmployee = employeeRepository.findById(employeeId).orElse(null);
        if (foundEmployee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ResponseObject("Failed", "Cannot find any employee with id: " + employeeId, null));
        }

        updatedEmployeeSchedule.setId(employeeScheduleId);
        updatedEmployeeSchedule.setEmployee(foundEmployee);
        return ResponseEntity.status(HttpStatus.OK)
        .body(new ResponseObject("OK", "Update employee schedule successfully", 
                                employeeScheduleRepository.save(updatedEmployeeSchedule)));
    }

    public ResponseEntity<ResponseObject> deleteEmployeeScheduleById(Long employeeScheduleId) {
        // Kiểm tra xem lịch có tồn tại không
        if (!employeeScheduleRepository.existsById(employeeScheduleId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ResponseObject("Failed", "Cannot find any schedule with id: " + employeeScheduleId, null));
        }
        employeeScheduleRepository.deleteById(employeeScheduleId);
        return ResponseEntity.status(HttpStatus.OK)
        .body(new ResponseObject("OK", "Delete employee schedule successfully", null)); 
    }
}
