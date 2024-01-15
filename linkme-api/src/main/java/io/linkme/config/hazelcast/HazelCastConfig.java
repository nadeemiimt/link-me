package io.linkme.config.hazelcast;

import io.linkme.model.JobListingDTO;
import io.linkme.util.JobListingSerializer;
import org.springframework.stereotype.Component;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

@Component
public class HazelCastConfig {

    public static final String JOBS = "jobs";
    private final HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(createConfig());

    public JobListingDTO put(Integer jobId, JobListingDTO jobListingDTO) {
        IMap<Integer, JobListingDTO> map = hazelcastInstance.getMap(JOBS);
        return map.putIfAbsent(jobId, jobListingDTO);
    }

    public JobListingDTO get(Integer jobId) {
        IMap<String, JobListingDTO> map = hazelcastInstance.getMap(JOBS);
        return map.get(jobId);
    }

    public Config createConfig() {
        Config config = new Config();
        config.addMapConfig(mapConfig());
        config.getSerializationConfig().addSerializerConfig(serializerConfig());
        return config;
    }

    private SerializerConfig serializerConfig() {
        return new SerializerConfig().setImplementation(new JobListingSerializer()).setTypeClass(JobListingDTO.class);
    }

    private MapConfig mapConfig() {
        MapConfig mapConfig = new MapConfig(JOBS);
        mapConfig.setTimeToLiveSeconds(360);
        mapConfig.setMaxIdleSeconds(400);
        return mapConfig;
    }

}
