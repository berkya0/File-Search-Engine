# 🔍 Desktop File Search Engine

![Status](https://img.shields.io/badge/Status-Geliştirme_Aşamasında-yellow)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-LTS-blue)

## 🎯 Projenin Amacı

Bu proje, yerel disk üzerindeki milyonlarca dosya arasında **yüksek performanslı** ve **anlık arama** yapabilmeyi hedefleyen bir arama motoru uygulamasıdır. 

İşletim sistemlerinin yerleşik arama servislerine alternatif olarak, daha hızlı ve özelleştirilebilir sonuçlar üretmek amacıyla şu mühendislik yaklaşımlarını temel alır:

* **Inverted Index Yapısı:** Kelime tabanlı aramaları milisaniyeler seviyesine indirmek için optimize edilmiş veri yapısı.
* **Çoklu İş Parçacığı (Multi-threading):** Milyonlarca dosyayı tararken işlemciyi (CPU) tam verimle kullanarak indeksleme süresini minimize etme.
* **Decoupled Architecture:** Backend (Spring Boot) ve Frontend (JavaFX) yapılarının birbirinden bağımsız, modüler bir şekilde haberleştiği modern mimari.

---

## 🛠️ Geliştirme Aşamaları

> **Not:** Aşağıdaki liste genel bir taslak olup, geliştirme sürecinde yeni özellikler eklenmeye devam edecektir.

- [x] Backend çekirdek yapısının kurulması (Spring Boot 3.x)
- [x] PostgreSQL veritabanı entegrasyonu ve veri modelleme
- [x] Dosya indeksleme mantığının temel implementasyonu
- [x] JavaFX ile temel seviye kullanıcı arayüzü (UI) tasarımı
- [ ] **Multi-threading** ile tarama performansının maksimize edilmesi
- [ ] **WatchService** ile gerçek zamanlı dosya sistem takibi
- [ ] Gelişmiş filtreleme (Dosya boyutu, uzantı, oluşturma tarihi vb.)
- [ ] Küresel hata yakalama (Global Exception Handling)

---

## 👨‍💻 Geliştirici
- **Kırıkkale Üniversitesi** - Bilgisayar Mühendisliği Öğrencisi
- [LinkedIn Profilin](https://www.linkedin.com/in/berkya)
