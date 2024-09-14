package hoversprite.project.image;

import hoversprite.project.request.ImageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public List<ImageDTO> createImages(List<ImageRequest> requests) {
        List<Image> images = requests.stream().map(ImageMapper.INSTANCE::toEntitySave).collect(Collectors.toList());
        return imageRepository.saveAll(images).stream().map(ImageMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ImageDTO> findImagesByFeedbackIds(List<Long> feedbackIds) {
        return imageRepository.getImagesByFeedbackIds(feedbackIds).stream().map(ImageMapper.INSTANCE::toDto).collect(Collectors.toList());
    }
}
