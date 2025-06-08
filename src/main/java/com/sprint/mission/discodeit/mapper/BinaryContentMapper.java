package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class BinaryContentMapper {

  @Autowired
  BinaryContentStorage binaryContentStorage;

  public abstract BinaryContentDto toDto(BinaryContent binaryContent);

  public abstract BinaryContent binaryContentDtoToBinaryContent(BinaryContentDto binaryContentDto);

  //XXX. 이 로직이 여기에 있는게 맞을까? 여기는 단순히 dto <-> entity 변환만 해줘야하는거아닌가?
  /* bytes[]는 별도 api 사용 */
//  @AfterMapping
//  protected void afterMapping(@MappingTarget BinaryContentDto binaryContentDto,
//      BinaryContent binaryContent) {
//
//    InputStream binaryContentBytes = this.binaryContentStorage.get(binaryContent.getId());
//
//    try {
//      byte[] bytes = BinaryContentConverter.toByteArray(binaryContentBytes);
//      binaryContentDto.setBytes(bytes);
//    } catch (IOException e) {
//      //FIXME : 올바른 타입으로 넣어주기
//      throw new RuntimeException();
//    }
//
//  }
}
