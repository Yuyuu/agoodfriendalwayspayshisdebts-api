package agoodfriendalwayspayshisdebts.model.event;

import agoodfriendalwayspayshisdebts.model.participant.Participant;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.vter.model.EntityWithUuid;

import java.util.List;
import java.util.UUID;

public class Event implements EntityWithUuid {

  private UUID id;
  private String name;
  private List<Participant> participants = Lists.newArrayList();

  /* This is used by mongolink */
  @SuppressWarnings("unused")
  protected Event() {}

  public Event(String name, List<Participant> participants) {
    id = UUID.randomUUID();
    this.name = name;
    this.participants.addAll(participants);
  }

  @Override
  public UUID getId() {
    return id;
  }

  public List<Participant> participants() {
    return participants;
  }

  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("id", id)
        .add("name", name)
        .toString();
  }
}