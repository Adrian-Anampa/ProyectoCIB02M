package pe.edu.objetoshallados.service;

import pe.edu.objetoshallados.model.Notice;
import pe.edu.objetoshallados.repository.AppRepository;

import java.util.List;

public final class NoticeService {
    private final AppRepository repository;

    public NoticeService(AppRepository repository) {
        this.repository = repository;
    }

    public List<Notice> forUser(long userId) {
        return repository.findNotices(userId);
    }

    public void markRead(long noticeId, long userId) {
        if (noticeId <= 0 || userId <= 0) throw new IllegalArgumentException("Selecciona un aviso válido.");
        repository.markNoticeRead(noticeId, userId);
    }
}
