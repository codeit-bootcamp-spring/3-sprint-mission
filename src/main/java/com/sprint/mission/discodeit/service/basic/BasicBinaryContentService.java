package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public BinaryContent create(BinaryContentCreateRequest createRequest) {
        //TODO : 에러처리
        byte[] byteArray = this.imageToByteArray(createRequest.contentFile());
        BinaryContent binaryContent = new BinaryContent(byteArray);
        this.binaryContentRepository.save(binaryContent);

        return binaryContent;
    }

    //reference : https://www.geeksforgeeks.org/java-program-to-convert-byte-array-to-image/
    static byte[] imageToByteArray(File imageFile) {
        System.out.println("imageFile.exists() : " + imageFile.exists());
        try {
            BufferedImage image = ImageIO.read(imageFile);
            try (
                    // 파일과 연결되는 스트림 생성
                    ByteArrayOutputStream outStreamObj = new ByteArrayOutputStream();
            ) {
                ImageIO.write(image, "png", outStreamObj);
                //Convert the image into the byte array.
                byte[] byteArray = outStreamObj.toByteArray();

                return byteArray;
            } catch (IOException e) {
                throw new IOException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    static Path byteArrayToImage(byte[] byteArray) {
        try (
                ByteArrayInputStream inStreambj = new ByteArrayInputStream(byteArray);
        ) {
            // read image from byte array
            BufferedImage newImage = ImageIO.read(inStreambj);

            Path imagePath = Paths.get(System.getProperty("user.dir", "imageOut")).resolve("outputImage" + UUID.randomUUID() + ".jpg");
            // write output image
            ImageIO.write(newImage, "jpg", new File(imagePath.toString()));

            return imagePath;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public BinaryContent find(UUID binaryContentId) {
        BinaryContent binaryContent = this.binaryContentRepository
                .findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("binaryContent with id " + binaryContentId + " not found"));

        return binaryContent;
    }


    // Reference : https://www.baeldung.com/java-filter-collection-by-list
    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds) {
        // TODO : 잘 작동하는지 확인할것
        List<BinaryContent> binaryContents = this.binaryContentRepository.findAll();
        Set<UUID> binaryContentIdsSet = new HashSet<>(binaryContentIds);

        List<BinaryContent> filteredBinaryContents = binaryContents.stream().filter(binaryContentIdsSet::contains).toList();

        return filteredBinaryContents;
    }

    @Override
    public void delete(UUID binaryContentId) {
        BinaryContent binaryContent = this.binaryContentRepository
                .findById(binaryContentId)
                .orElseThrow(() -> new NoSuchElementException("binaryContent with id " + binaryContentId + " not found"));

        this.binaryContentRepository.deleteById(binaryContentId);
    }
}
