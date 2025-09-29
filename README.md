Bài 1

CRUD với Users

Login để lấy token
![alt text](image.png)

GET all users
![alt text](image-1.png)


POST tạo user mới 
![alt text](image-2.png)
PUT cập nhật user 
![alt text](image-3.png)
DELETE user 
![alt text](image-4.png)
CRUD với Blogs

GET all blogs 
![alt text](image-5.png)
POST tạo blog mới
![alt text](image-6.png)


PUT cập nhật blog 
![alt text](image-7.png)
DELETE blog 
![alt text](image-8.png)
Bài 2

Sử dụng Spring Security + JWT để đăng nhập và sinh token.
![alt text](image-9.png)

Kiểm tra role trong DB(có trường role (admin/user)
![alt text](image-10.png)


Chỉ ADMIN mới được xóa user(xóa user id 3)
![alt text](image-11.png)


🡺thành công 200

Nếu user xóa user (xóa user id 4)
![alt text](image-12.png)

Thất bại 403
USER chỉ được xem / cập nhật blog của chính mình

Xem blog
![alt text](image-13.png)


Tạo blog với user A
![alt text](image-14.png)


User xem và cập nhật blog của chính mình
![alt text](image-15.png)