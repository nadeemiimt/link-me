package io.linkme.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import io.linkme.model.JobListingDTO;

public class JobListingSerializer implements StreamSerializer<JobListingDTO> {

    @Override
    public void write(ObjectDataOutput out, JobListingDTO object) throws IOException {
        out.writeInt(object.getJobId());
        out.writeString(object.getTitle());
        out.writeString(object.getJobRole());
        out.writeString(object.getJobType());
        out.writeString(object.getLocation());
        out.writeFloat(object.getExperience().floatValue());
        out.writeStringArray(object.getSkills().toArray(new String[0]));
    }

    @Override
    public JobListingDTO read(ObjectDataInput in) throws IOException {
        return JobListingDTO.builder()
                .jobId(in.readInt())
                .title(in.readString())
                .jobRole(in.readString())
                .jobType(in.readString())
                .location(in.readString())
                .experience(BigDecimal.valueOf(in.readFloat()))
                .skills(List.of(in.readStringArray())).build();
    }

    @Override
    public int getTypeId() {
        return 1;
    }

}
