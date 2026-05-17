package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
@Service
public class TrainService {

    private List<Train> trainList;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAINS_FILE = "app/src/main/resources/trains.json";

    public TrainService() throws IOException {
        loadTrains();
    }

    private void loadTrains() throws IOException {
        File file = new File(TRAINS_FILE);
        if (file.exists()) {
            trainList = objectMapper.readValue(file, new TypeReference<List<Train>>() {});
        } else {
            trainList = new ArrayList<>();
        }
    }

    public void saveTrains() throws IOException {
        File file = new File(TRAINS_FILE);
        file.getParentFile().mkdirs();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, trainList);
    }

    public List<Train> searchTrains(String source, String destination) {
        return trainList.stream()
                .filter(train -> {
                    List<String> stations = train.getStations();
                    if (stations == null) return false;
                    int srcIdx = stations.indexOf(source.toLowerCase());
                    int dstIdx = stations.indexOf(destination.toLowerCase());
                    return srcIdx != -1 && dstIdx != -1 && srcIdx < dstIdx;
                })
                .collect(Collectors.toList());
    }


    public Optional<Train> getTrainById(String trainId) {
        return trainList.stream()
                .filter(t -> t.getTrainId().equals(trainId))
                .findFirst();
    }

    public void addTrain(Train train) throws IOException {
        trainList.add(train);
        saveTrains();
    }

    public List<Train> getAllTrains() {
        return trainList;
    }
}
