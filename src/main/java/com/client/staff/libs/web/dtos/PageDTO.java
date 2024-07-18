package com.client.staff.libs.web.dtos;

import java.util.List;
import org.springframework.data.domain.Sort;

public class PageDTO<T> {
    protected List<T> content;
    protected boolean first;
    protected boolean last;
    protected int totalPages;
    protected long totalElements;
    protected int numberOfElements;
    protected int size;
    protected int number;
    protected List<Sort.Order> sort;

    PageDTO(final List<T> content, final boolean first, final boolean last, final int totalPages, final long totalElements, final int numberOfElements, final int size, final int number, final List<Sort.Order> sort) {
        this.content = content;
        this.first = first;
        this.last = last;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.numberOfElements = numberOfElements;
        this.size = size;
        this.number = number;
        this.sort = sort;
    }

    public static <T> PageDTOBuilder<T> builder() {
        return new PageDTOBuilder();
    }

    public List<T> getContent() {
        return this.content;
    }

    public boolean isFirst() {
        return this.first;
    }

    public boolean isLast() {
        return this.last;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public long getTotalElements() {
        return this.totalElements;
    }

    public int getNumberOfElements() {
        return this.numberOfElements;
    }

    public int getSize() {
        return this.size;
    }

    public int getNumber() {
        return this.number;
    }

    public List<Sort.Order> getSort() {
        return this.sort;
    }

    public void setContent(final List<T> content) {
        this.content = content;
    }

    public void setFirst(final boolean first) {
        this.first = first;
    }

    public void setLast(final boolean last) {
        this.last = last;
    }

    public void setTotalPages(final int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalElements(final long totalElements) {
        this.totalElements = totalElements;
    }

    public void setNumberOfElements(final int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public void setSort(final List<Sort.Order> sort) {
        this.sort = sort;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PageDTO)) {
            return false;
        } else {
            PageDTO<?> other = (PageDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isFirst() != other.isFirst()) {
                return false;
            } else if (this.isLast() != other.isLast()) {
                return false;
            } else if (this.getTotalPages() != other.getTotalPages()) {
                return false;
            } else if (this.getTotalElements() != other.getTotalElements()) {
                return false;
            } else if (this.getNumberOfElements() != other.getNumberOfElements()) {
                return false;
            } else if (this.getSize() != other.getSize()) {
                return false;
            } else if (this.getNumber() != other.getNumber()) {
                return false;
            } else {
                Object this$content = this.getContent();
                Object other$content = other.getContent();
                if (this$content == null) {
                    if (other$content != null) {
                        return false;
                    }
                } else if (!this$content.equals(other$content)) {
                    return false;
                }

                Object this$sort = this.getSort();
                Object other$sort = other.getSort();
                if (this$sort == null) {
                    if (other$sort != null) {
                        return false;
                    }
                } else if (!this$sort.equals(other$sort)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PageDTO;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        result = result * 59 + (this.isFirst() ? 79 : 97);
        result = result * 59 + (this.isLast() ? 79 : 97);
        result = result * 59 + this.getTotalPages();
        long $totalElements = this.getTotalElements();
        result = result * 59 + (int)($totalElements >>> 32 ^ $totalElements);
        result = result * 59 + this.getNumberOfElements();
        result = result * 59 + this.getSize();
        result = result * 59 + this.getNumber();
        Object $content = this.getContent();
        result = result * 59 + ($content == null ? 43 : $content.hashCode());
        Object $sort = this.getSort();
        result = result * 59 + ($sort == null ? 43 : $sort.hashCode());
        return result;
    }

    public String toString() {
        List var10000 = this.getContent();
        return "PageDTO(content=" + var10000 + ", first=" + this.isFirst() + ", last=" + this.isLast() + ", totalPages=" + this.getTotalPages() + ", totalElements=" + this.getTotalElements() + ", numberOfElements=" + this.getNumberOfElements() + ", size=" + this.getSize() + ", number=" + this.getNumber() + ", sort=" + this.getSort() + ")";
    }

    public static class PageDTOBuilder<T> {
        private List<T> content;
        private boolean first;
        private boolean last;
        private int totalPages;
        private long totalElements;
        private int numberOfElements;
        private int size;
        private int number;
        private List<Sort.Order> sort;

        PageDTOBuilder() {
        }

        public PageDTOBuilder<T> content(final List<T> content) {
            this.content = content;
            return this;
        }

        public PageDTOBuilder<T> first(final boolean first) {
            this.first = first;
            return this;
        }

        public PageDTOBuilder<T> last(final boolean last) {
            this.last = last;
            return this;
        }

        public PageDTOBuilder<T> totalPages(final int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public PageDTOBuilder<T> totalElements(final long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public PageDTOBuilder<T> numberOfElements(final int numberOfElements) {
            this.numberOfElements = numberOfElements;
            return this;
        }

        public PageDTOBuilder<T> size(final int size) {
            this.size = size;
            return this;
        }

        public PageDTOBuilder<T> number(final int number) {
            this.number = number;
            return this;
        }

        public PageDTOBuilder<T> sort(final List<Sort.Order> sort) {
            this.sort = sort;
            return this;
        }

        public PageDTO<T> build() {
            return new PageDTO(this.content, this.first, this.last, this.totalPages, this.totalElements, this.numberOfElements, this.size, this.number, this.sort);
        }

        public String toString() {
            return "PageDTO.PageDTOBuilder(content=" + this.content + ", first=" + this.first + ", last=" + this.last + ", totalPages=" + this.totalPages + ", totalElements=" + this.totalElements + ", numberOfElements=" + this.numberOfElements + ", size=" + this.size + ", number=" + this.number + ", sort=" + this.sort + ")";
        }
    }
}