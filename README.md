# Ứng dụng Chat Client-Server với Mã hóa Playfair

Ứng dụng chat client-server sử dụng giao thức TCP, cho phép người dùng gửi tin nhắn văn bản và tệp (âm thanh, video, hình ảnh). Tin nhắn văn bản được mã hóa bằng thuật toán Playfair trước khi gửi đến server và giải mã khi nhận được.

## Tính năng chính

- **Mã hóa Playfair**: Mã hóa và giải mã tin nhắn văn bản bằng thuật toán Playfair
- **Truyền tệp**: Hỗ trợ gửi và nhận các loại tệp khác nhau (hình ảnh, âm thanh, video)
- **Tìm kiếm cụm từ**: Server tìm kiếm cụm từ "xin chào" trong tin nhắn đã giải mã và trả về vị trí
- **Lưu trữ dữ liệu**: Lưu trữ tin nhắn và thông tin truyền tệp trong cơ sở dữ liệu PostgreSQL
- **Giao diện đồ họa**: Giao diện người dùng thân thiện xây dựng bằng Java Swing

## Yêu cầu hệ thống

- Java Development Kit (JDK) 21 trở lên
- Gradle 8.8 trở lên
- Kết nối Internet (để kết nối đến cơ sở dữ liệu PostgreSQL)

## Cài đặt

### Sử dụng Gradle

1. Clone repository:
   ```
   git clone https://github.com/yourusername/ChatClientServer-LTM.git
   cd ChatClientServer-LTM
   ```

2. Build dự án:

   ```
   ./gradlew build
   ```

3. Tạo file JAR:

   ```
   ./gradlew jar
   ```

## Chạy ứng dụng

### Sử dụng script chạy tự động

1. **Windows**: Chạy file `run.bat` bằng cách nhấp đúp vào nó hoặc chạy lệnh:
   ```
   run.bat
   ```

2. **Linux/Mac**: Chạy file `run.sh` bằng lệnh:
   ```
   chmod +x run.sh
   ./run.sh
   ```

### Sử dụng Gradle

```
./gradlew run
```

Hoặc trên Windows:
```
gradlew.bat run
```

**Lưu ý:** Nếu bạn gặp vấn đề khi chạy bằng Gradle, hãy sử dụng phương pháp chạy file JAR như hướng dẫn bên dưới.

### Sử dụng file JAR

1. Chạy toàn bộ ứng dụng (cả client và server):
   ```
   java -jar app\build\libs\app.jar
   ```

   Hoặc trên Linux/Mac:
   ```
   java -jar app/build/libs/app.jar
   ```

2. Chạy chỉ server:
   ```
   java -cp app\build\libs\app.jar chatclientserver.ltm.server.ServerMain
   ```

   Hoặc trên Linux/Mac:
   ```
   java -cp app/build/libs/app.jar chatclientserver.ltm.server.ServerMain
   ```

3. Chạy chỉ client:
   ```
   java -cp app\build\libs\app.jar chatclientserver.ltm.client.ClientMain
   ```

   Hoặc trên Linux/Mac:
   ```
   java -cp app/build/libs/app.jar chatclientserver.ltm.client.ClientMain
   ```

## Hướng dẫn sử dụng

### Chọn chế độ chạy

Khi chạy ứng dụng, bạn sẽ thấy một hộp thoại cho phép chọn:
- **Client**: Chạy chỉ ứng dụng client
- **Server**: Chạy chỉ server
- **Both**: Chạy cả client và server
- **Exit**: Thoát ứng dụng

### Sử dụng Client

1. Khi client khởi động, bạn sẽ thấy giao diện với các thành phần sau:
   - Ô nhập văn bản cho tin nhắn
   - Ô nhập khóa cho thuật toán Playfair (mặc định là "PLAYFAIR")
   - Nút "Connect" để kết nối đến server
   - Nút "Send" để gửi tin nhắn
   - Nút "Send File" để gửi tệp
   - Nút "Exchange Key" để trao đổi khóa với server

2. Nhấn nút "Connect" để kết nối đến server.

3. Nhập tin nhắn vào ô nhập văn bản và nhấn "Send" để gửi tin nhắn. Tin nhắn sẽ được mã hóa bằng thuật toán Playfair với khóa đã nhập trước khi gửi đến server.

4. Nếu tin nhắn của bạn chứa cụm từ "xin chào", server sẽ tìm và trả về vị trí của cụm từ này trong tin nhắn đã giải mã.

5. Để gửi tệp, nhấn nút "Send File" và chọn tệp từ hộp thoại chọn tệp.

6. Để thay đổi khóa mã hóa, nhập khóa mới vào ô khóa và nhấn "Exchange Key".

### Sử dụng Server

Server chạy tự động và xử lý các kết nối từ client. Nó sẽ:
1. Nhận tin nhắn đã mã hóa từ client
2. Giải mã tin nhắn bằng thuật toán Playfair
3. Tìm cụm từ "xin chào" trong tin nhắn đã giải mã
4. Gửi vị trí của cụm từ về cho client
5. Lưu trữ tin nhắn và kết quả tìm kiếm vào cơ sở dữ liệu PostgreSQL

## Cấu trúc dự án

```
app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── chatclientserver/
│   │   │       └── ltm/
│   │   │           ├── client/         # Mã nguồn client
│   │   │           ├── database/       # Kết nối và truy vấn cơ sở dữ liệu
│   │   │           ├── encryption/     # Thuật toán mã hóa Playfair
│   │   │           ├── model/          # Các lớp mô hình dữ liệu
│   │   │           ├── server/         # Mã nguồn server
│   │   │           ├── util/           # Các tiện ích
│   │   │           └── App.java        # Lớp chính của ứng dụng
│   │   └── resources/                  # Tài nguyên
│   └── test/                           # Mã nguồn kiểm thử
└── build.gradle                        # Cấu hình build Gradle
```

## Công nghệ sử dụng

- **Ngôn ngữ lập trình:** Java
- **Giao diện người dùng:** Java Swing
- **Cơ sở dữ liệu:** PostgreSQL
- **Mã hóa:** Thuật toán Playfair
- **Build tool:** Gradle
- **Design Patterns:** Singleton, Observer, MVC, Factory, Strategy

## Cơ sở dữ liệu

Ứng dụng sử dụng cơ sở dữ liệu PostgreSQL với schema sau:

- **Bảng Messages**
  - `id` (Khóa chính, tự động tăng)
  - `client_id` (Định danh của client)
  - `encrypted_message` (Văn bản mã hóa)
  - `key` (Khóa mã hóa)
  - `decrypted_message` (Văn bản đã giải mã)
  - `phrase_positions` (Văn bản, lưu các vị trí dưới dạng chuỗi phân tách bằng dấu phẩy)
  - `timestamp` (Thời gian nhận tin nhắn)

- **Bảng FileTransfers**
  - `id` (Khóa chính, tự động tăng)
  - `client_id` (Định danh của client)
  - `file_name` (Tên tệp)
  - `file_size` (Kích thước tệp)
  - `file_type` (Loại tệp)
  - `timestamp` (Thời gian truyền tệp)

## Thuật toán Playfair

Thuật toán Playfair là một kỹ thuật mã hóa thay thế sử dụng ma trận 5x5 các chữ cái. Các bước cơ bản của thuật toán:

1. Tạo ma trận 5x5 từ khóa (loại bỏ các chữ cái trùng lặp)
2. Chia văn bản thành các cặp chữ cái (digraphs)
3. Áp dụng quy tắc mã hóa cho từng cặp:
   - Nếu hai chữ cái nằm trên cùng một hàng, thay thế bằng chữ cái bên phải của mỗi chữ cái
   - Nếu hai chữ cái nằm trên cùng một cột, thay thế bằng chữ cái bên dưới của mỗi chữ cái
   - Nếu hai chữ cái tạo thành một hình chữ nhật, thay thế bằng chữ cái ở cùng hàng nhưng ở góc đối diện của hình chữ nhật

## Vấn đề thường gặp

### Không thể chạy bằng lệnh `./gradlew run`

Nếu bạn gặp vấn đề khi chạy ứng dụng bằng lệnh `./gradlew run` hoặc `gradlew.bat run`, hãy thử các giải pháp sau:

1. **Sử dụng script chạy tự động**:
   - Windows: Chạy file `run.bat`
   - Linux/Mac: Chạy file `run.sh`

2. **Sử dụng file JAR trực tiếp**:
   ```
   java -jar app\build\libs\app.jar
   ```

3. **Chạy riêng server và client**:
   ```
   java -cp app\build\libs\app.jar chatclientserver.ltm.server.ServerMain
   ```
   Và trong một terminal khác:
   ```
   java -cp app\build\libs\app.jar chatclientserver.ltm.client.ClientMain
   ```

4. **Kiểm tra các cổng đang sử dụng**: Nếu bạn gặp lỗi "Address already in use", có thể cổng 8888 đã được sử dụng. Hãy sử dụng lệnh sau để kiểm tra và kết thúc các tiến trình đang sử dụng cổng đó:
   ```
   netstat -ano | findstr :8888
   taskkill /PID <PID> /F
   ```

### Không thể tạo file JAR

Nếu bạn gặp vấn đề khi tạo file JAR, hãy thử các giải pháp sau:

1. **Sử dụng script chạy tự động để build**:
   - Windows: Chạy file `run.bat` và chọn tùy chọn 4 (Build the application)
   - Linux/Mac: Chạy file `run.sh` và chọn tùy chọn 4 (Build the application)

2. **Build thủ công**:
   ```
   ./gradlew clean build jar
   ```
   Hoặc trên Windows:
   ```
   gradlew.bat clean build jar
   ```

## Liên hệ

Nếu bạn có bất kỳ câu hỏi hoặc góp ý nào, vui lòng liên hệ qua email: 52100781@student.tdtu.edu.vn
