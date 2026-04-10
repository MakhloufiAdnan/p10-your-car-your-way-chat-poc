import { Component, inject, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-message-composer',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './message-composer.component.html',
  styleUrl: './message-composer.component.scss',
})
export class MessageComposerComponent {
  private readonly fb = inject(FormBuilder);

  readonly disabled = input(false);
  readonly isSubmitting = input(false);
  readonly errorMessage = input('');
  readonly messageSubmitted = output<string>();

  protected readonly form = this.fb.nonNullable.group({
    content: ['', [Validators.required, Validators.maxLength(2000)]],
  });

  protected onSubmit(): void {
    if (this.disabled() || this.isSubmitting()) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const content = this.form.getRawValue().content.trim();

    if (!content) {
      this.form.controls.content.setErrors({ required: true });
      return;
    }

    this.messageSubmitted.emit(content);
  }

  protected clearForm(): void {
    this.form.reset({
      content: '',
    });
  }

  public reset(): void {
    this.clearForm();
  }
}
