import urllib.request
import os

# Bikin folder buat nyimpen gambar kalau belum ada
folder_name = "pokemon_gen4_png"
os.makedirs(folder_name, exist_ok=True)

# ID Pokemon Gen 4 mulai dari 387 (Turtwig) sampai 493 (Arceus)
print(f"Mulai menyedot 107 gambar Pokemon Gen 4 ke folder '{folder_name}'...")

for poke_id in range(387, 494):
    # Link Official Artwork Kualitas HD
    url = f"https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/{poke_id}.png"
    file_path = f"{folder_name}/{poke_id}.png"
    
    print(f"Downloading ID {poke_id}...", end=" ")
    try:
        # Download gambarnya
        urllib.request.urlretrieve(url, file_path)
        print("SUKSES!")
    except Exception as e:
        print("GAGAL!")

print("\nSELESAI COO! Semua gambar udah siap dipakai di game lu!")