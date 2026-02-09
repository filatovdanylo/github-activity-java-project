package pj.adapter.in.rest.dtos;

import pj.domain.model.Activity;
import java.util.List;

public record ActivityResponseDto(String username, int activityCount, List<Activity> activities) {

}
