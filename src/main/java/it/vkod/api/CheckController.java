package it.vkod.api;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import it.vkod.models.entities.Check;
import it.vkod.models.entities.Course;
import it.vkod.models.entities.Event;
import it.vkod.services.flow.CheckService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(path = CheckController.BASE_ENDPOINT)
public class CheckController {

    public static final String BASE_ENDPOINT = "/api/v1/checks";

    private final CheckService checkService;

    public CheckController(final CheckService checkService) {
        this.checkService = checkService;
    }

    @GetMapping("today")
    public List<Check> today() {

        return checkService.today();
    }

    @GetMapping("on")
    public List<Check> on(@RequestParam final java.sql.Date on) {

        return checkService.on(on);
    }

    @GetMapping("attendee/{attendee_username}")
    public List<Check> ofAttendee(@RequestParam java.sql.Date on, @RequestParam final java.sql.Time at,
            @PathVariable("attendee_username") final String attendee) {

        return checkService.ofAttendee(on, at, attendee);
    }

    @GetMapping("organizer/{organizer_username}")
    public List<Check> fromOrganizer(@RequestParam final java.sql.Date on, @RequestParam final java.sql.Time at,
            @PathVariable("organizer_username") final String username) {

        return checkService.fromOrganizer(on, at, username);
    }

    @PostMapping("in")
    public Optional<Check> checkin(@RequestParam @NotEmpty final String session, @RequestParam @NotEmpty final String course,
                                   @RequestParam @NotEmpty final String organizer, @RequestParam @NotEmpty final String attendee,
                                   @RequestParam(required = false) @NotEmpty final Double latitude,
                                   @RequestParam(required = false) @NotEmpty final Double longitude,
                                   @RequestParam(required = false) final boolean remote, @RequestParam(required = false) final boolean guest) {

        return checkService.checkin(session, Course.valueOf(course), organizer, attendee, latitude, longitude, remote,
                guest);

    }

    @PostMapping("out")
    public Optional<Check> checkout(@RequestParam @NotEmpty final String session, @RequestParam @NotEmpty final String course,
                                    @RequestParam @NotEmpty final String organizer, @RequestParam @NotEmpty final String attendee,
                                    @NotEmpty final Double latitude, @NotEmpty final Double longitude,
                                    @RequestParam(required = false) final boolean remote, @RequestParam(required = false) final boolean guest) {

        return checkService.checkout(session, Course.valueOf(course), organizer, attendee, latitude, longitude, remote,
                guest);

    }

    @PostMapping("in/json")
    public Optional<Check> createOrUpdate(@RequestBody @Valid final Check check) {

        return checkService.createOrUpdate(check);

    }

    @GetMapping("course/{course_id}")
    public List<Check> fromCourse(@PathVariable("course_id") final String course) {

        return checkService.fromCourse(Course.valueOf(course));
    }

    @GetMapping("course_with_events/{course_id}")
    public List<Check> fetchAllByCourse(@PathVariable("course_id") final String course,
                                                  @RequestParam final Set<Event> types) {

        return checkService.fetchAllByCourse(Course.valueOf(course), types.toArray(Event[]::new));
    }

}
