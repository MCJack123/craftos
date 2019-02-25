#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <cstdint>
#define inv(n) n = !n
#include <png++/png.hpp>

bool operator==(png::rgb_pixel lhs, png::rgb_pixel rhs) {
    return lhs.red == rhs.red && lhs.green == rhs.green && lhs.blue == rhs.blue;
}

int findVec(std::vector<png::rgb_pixel> vec, png::rgb_pixel val) {
    for (int i = 0; i < vec.size(); i++)
        if (vec[i].red == val.red && vec[i].green == val.green && vec[i].blue == val.blue) return i;
    return -1;
}

int main(int argc, const char * argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " <input.png> <output.ccbmp>\n";
        return 1;
    }
    png::image<png::rgb_pixel> input(argv[1]);
    std::stringstream out;
    std::vector<png::rgb_pixel> palette;
    uint16_t width = (uint16_t)input.get_width();
    uint16_t height = (uint16_t)input.get_height();
    if (width > 306 || height > 171) {
        std::cerr << "Error: Image too large (maximum 306x171)\n";
        return 3;
    }
    uint8_t num = 0;
    bool ubit = false;
    out.write("cbmp", 4);
    out.write((char*)&width, 2);
    out.write((char*)&height, 2);
    for (size_t y = 0; y < height; y++) {
        for (size_t x = 0; x < width; x++) {
            png::rgb_pixel p = input.get_pixel(x, y);
            int it = findVec(palette, p);
            uint8_t index;
            if (it == -1) {
                if (palette.size() >= 16) {
                    std::cerr << "Error: Image contains more than 16 colors.\n";
                    return 2;
                }
                index = palette.size();
                palette.push_back(p);
                std::cout << "Found color " << (int)index << " with RGB " << (int)p.red << ", " << (int)p.green << ", " << (int)p.blue << "\n";
            } else index = it;
            if (ubit) {
                num |= index;
                out.put(num);
                num = 0;
            } else num = index << 4;
            inv(ubit);
        }
    }
    if (ubit) out.put(num);
    out.put((uint8_t)palette.size());
    for (png::rgb_pixel p : palette) {
        out.put(p.red);
        out.put(p.green);
        out.put(p.blue);
    }
    std::ofstream fout(argv[2]);
    fout << out.rdbuf();
    fout.close();
    std::cout << "Wrote image to " << argv[2] << "\n";
    return 0;
}