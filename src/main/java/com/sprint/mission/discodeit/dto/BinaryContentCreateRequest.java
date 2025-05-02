package com.sprint.mission.discodeit.dto;

import java.io.File;

//XXX. 유저아이디가 없어도되나? 나중에 삭제하고싶을때 본인이 올린건지 확인안하나?
// -> 메세지도 userId있고, user에도 profileId 있어서 그걸로 확인하면 될듯?
public record BinaryContentCreateRequest(File contentFile) {
}

