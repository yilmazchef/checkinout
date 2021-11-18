echo Y. | rmdir /s .mysql\\data && ^
mkdir .mysql\\data && ^
.mysql\\bin\\mysqld.exe --initialize-insecure --console && ^
.mysql\\bin\\mysqld.exe