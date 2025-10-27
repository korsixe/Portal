package com.mipt.portal.moderator;

public interface ModeratorService {

  // Управление объявлениями
  List<Ad> getModerationQueue();
  void approveAdvertisement(Long advertisementId, Long moderatorId);
  void rejectAdvertisement(Long advertisementId, String reason, Long moderatorId);
  void hideAdvertisement(Long advertisementId, String reason, Long moderatorId);

  // Борьба с нарушениями
  void blockUser(Long userId, BlockDuration duration, String reason, Long moderatorId);
  void unblockUser(Long userId, Long moderatorId);
  List<UserViolation> getUserViolationHistory(Long userId);

  // Работа с жалобами
  List<Complaint> getComplaintQueue(ComplaintFilter filter);
  void resolveComplaint(Long complaintId, ComplaintResolution resolution, Long moderatorId);
  Complaint getComplaintDetails(Long complaintId);
}