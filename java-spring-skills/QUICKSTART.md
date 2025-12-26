# QUICKSTART - Chạy trong 5 phút

## Yêu cầu

- Java 21+ ([Download](https://adoptium.net/))
- Maven 3.9+ ([Download](https://maven.apache.org/download.cgi))
- Docker Desktop ([Download](https://www.docker.com/products/docker-desktop/))

## Bước 1: Khởi động Database

```powershell
# Mở PowerShell, cd đến thư mục project
cd d:\1.AI\3.projects\java\skills\java-spring-skills

# Khởi động PostgreSQL
docker-compose up -d postgres

# Đợi 10 giây cho database khởi động
Start-Sleep -Seconds 10

# Kiểm tra đã chạy chưa
docker-compose ps
```

**Kết quả mong đợi:**
```
NAME                    STATUS
java-spring-postgres    Up
```

## Bước 2: Chạy Application

```powershell
# Chạy ứng dụng
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Kết quả mong đợi:**
```
Started Application in X.XXX seconds
```

## Bước 3: Test API

Mở trình duyệt hoặc dùng curl:

```powershell
# Health check
curl http://localhost:8080/actuator/health

# Xem modules đang bật
curl http://localhost:8080/api/system/modules

# Test API users
curl http://localhost:8080/api/users
```

## Bước 4: Chạy Tests

```powershell
# Mở terminal mới (giữ app đang chạy)
mvn test
```

---

## Lỗi thường gặp

### "Port 5432 already in use"
```powershell
# Dừng PostgreSQL local nếu đang chạy
Stop-Service postgresql*
# Hoặc đổi port trong docker-compose.yml
```

### "Connection refused"
```powershell
# Kiểm tra Docker đang chạy
docker ps

# Khởi động lại database
docker-compose down
docker-compose up -d postgres
```

### "Java version error"
```powershell
# Kiểm tra version
java -version

# Phải là 21 trở lên
```

---

## Các lệnh hữu ích

| Lệnh | Mô tả |
|------|-------|
| `docker-compose up -d postgres` | Chỉ chạy PostgreSQL |
| `docker-compose up -d` | Chạy tất cả services |
| `docker-compose down` | Dừng tất cả |
| `docker-compose logs -f` | Xem logs |
| `mvn clean compile` | Build project |
| `mvn test` | Chạy tests |
| `mvn spring-boot:run` | Chạy app |

---

## Tiếp theo

Sau khi chạy được, đọc thêm:
- [README.md](README.md) - Hướng dẫn đầy đủ
- [openspec/AGENTS.md](openspec/AGENTS.md) - Quy tắc cho AI agents
- [SKILL.md](SKILL.md) - Tài liệu skill chi tiết
