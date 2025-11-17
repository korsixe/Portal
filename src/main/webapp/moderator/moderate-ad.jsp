<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.mipt.portal.announcement.AdsService" %>
<%@ page import="com.mipt.portal.announcement.Announcement" %>
<%@ page import="com.mipt.portal.announcement.AdvertisementStatus" %>
<%@ page import="com.mipt.portal.moderator.message.ModerationMessageService" %>
<%
    // Проверка авторизации модератора
    if (session.getAttribute("moderator") == null) {
        response.sendRedirect("login-moderator.jsp");
        return;
    }

    String action = request.getParameter("action");
    String adIdParam = request.getParameter("adId");
    String reason = request.getParameter("reason");
    String moderatorEmail = (String) session.getAttribute("moderatorEmail");
    String message = "Действие выполнено";
    String messageType = "success";

    if (action != null && adIdParam != null) {
        try {
            Long adId = Long.parseLong(adIdParam);
            AdsService adsService = new AdsService();
            Announcement ad = adsService.getAd(adId);

            if (ad != null) {
                // Используем новый сервис для логирования действий модератора
                ModerationMessageService.logModerationAction(
                        adId,
                        action,
                        reason,
                        moderatorEmail
                );

                // Используем только существующие статусы из AdvertisementStatus
                switch (action) {
                    case "approve":
                        ad.setStatus(AdvertisementStatus.ACTIVE);
                        message = "Объявление одобрено";
                        break;
                    case "reject":
                        ad.setStatus(AdvertisementStatus.DRAFT);
                        message = "Объявление отправлено на доработку";
                        break;
                    case "delete":
                        ad.setStatus(AdvertisementStatus.DELETED);
                        message = "Объявление удалено";
                        break;
                }

                adsService.editAd(ad);
            } else {
                message = "Объявление не найдено";
                messageType = "error";
            }
        } catch (Exception e) {
            message = "Произошла ошибка: " + e.getMessage();
            messageType = "error";
        }
    } else {
        message = "Неверные параметры";
        messageType = "error";
    }

    response.sendRedirect("moderation-bord.jsp?message=" +
            java.net.URLEncoder.encode(message, "UTF-8") +
            "&messageType=" + messageType);
%>